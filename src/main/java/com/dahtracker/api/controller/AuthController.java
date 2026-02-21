package com.dahtracker.api.controller;

import com.dahtracker.api.config.JwtUtils;
import com.dahtracker.api.model.Role;
import com.dahtracker.api.model.User;
import com.dahtracker.api.dto.*;
import com.dahtracker.api.service.UserService;
import com.dahtracker.api.service.EmailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    // 22-2-2025: change login signup feature
    private final EmailService emailService;
    // 22-2-2025: change login signup feature - For loading user details (JWT generation)
    private final org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private static final int PIN_EXPIRY_MINUTES = 15;
    private static final int RESET_TOKEN_EXPIRY_HOURS = 1;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Email already in use"));
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(Role.USER);
        // 22-2-2025: change login signup feature - User needs to verify email before being enabled
        user.setEnabled(false);
        user.setVerified(false);

        // Generate verification PIN
        String pin = generateVerificationPin();
        user.setVerificationPin(passwordEncoder.encode(pin));
        user.setPinExpiry(LocalDateTime.now().plusMinutes(PIN_EXPIRY_MINUTES));

        userService.createUser(user);

        // Send verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getName(), pin);
        } catch (Exception e) {
            // Log but don't fail registration if email fails
            System.err.println("Failed to send verification email: " + e.getMessage());
        }

        return ResponseEntity.ok(new MessageResponse(
                "Registration successful! Please check your email for a 6-digit verification code."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        User user = userService.findByEmail(request.getEmail()).orElseThrow();

        return ResponseEntity.ok(new LoginResponse(jwt, user.getEmail(), user.getName(), roles, user.getAvatarUrl(), user.getId(), user.getCreatedAt()));
    }

    // 22-2-2025: change login signup feature - Verify email with PIN
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("User not found"));
        }

        if (user.getVerified()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Email already verified"));
        }

        // Check if PIN expired
        if (user.getPinExpiry() == null || user.getPinExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Verification code expired. Please request a new one."));
        }

        // Verify PIN
        if (!passwordEncoder.matches(request.getPin(), user.getVerificationPin())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid verification code"));
        }

        // Mark as verified and enable
        user.setVerified(true);
        user.setEnabled(true);
        user.setVerificationPin(null);
        user.setPinExpiry(null);
        userService.updateUser(user);

        return ResponseEntity.ok(new MessageResponse("Email verified successfully! You can now login."));
    }

    // 22-2-2025: change login signup feature - Resend verification email
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("User not found"));
        }

        if (user.getVerified()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Email already verified"));
        }

        // Generate new PIN
        String pin = generateVerificationPin();
        user.setVerificationPin(passwordEncoder.encode(pin));
        user.setPinExpiry(LocalDateTime.now().plusMinutes(PIN_EXPIRY_MINUTES));
        userService.updateUser(user);

        // Send verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getName(), pin);
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Failed to send verification email. Please try again."));
        }

        return ResponseEntity.ok(new MessageResponse("Verification code sent! Please check your email."));
    }

    // 22-2-2025: change login signup feature - Request password reset
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElse(null);

        // Always return success to prevent email enumeration
        if (user == null) {
            return ResponseEntity.ok(new MessageResponse(
                    "If that email is registered, you'll receive a password reset link shortly."));
        }

        // Generate reset token
        String resetToken = generateResetToken();
        user.setResetToken(passwordEncoder.encode(resetToken));
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(RESET_TOKEN_EXPIRY_HOURS));
        userService.updateUser(user);

        // Send password reset email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetToken);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
        }

        return ResponseEntity.ok(new MessageResponse(
                "If that email is registered, you'll receive a password reset link shortly."));
    }

    // 22-2-2025: change login signup feature - Reset password with token
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        // Find user by reset token (need to add method to UserRepository or iterate)
        User user = userService.findByResetToken(request.getToken())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid or expired reset token"));
        }

        // Check if token expired
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Reset token expired. Please request a new one."));
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userService.updateUser(user);

        return ResponseEntity.ok(new MessageResponse("Password reset successfully! You can now login with your new password."));
    }

    // Helper method to generate 6-digit PIN
    private String generateVerificationPin() {
        Random random = new SecureRandom();
        int pin = 100000 + random.nextInt(900000); // 6-digit number
        return String.valueOf(pin);
    }

    // Helper method to generate reset token
    private String generateResetToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 64; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }

    // 22-2-2025: change login signup feature - Google OAuth login
    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        try {
            // Validate Google ID token using Google's TokenInfo API
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + request.getIdToken();

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.containsKey("error")) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Invalid Google token"));
            }

            // Extract user info from Google response
            String googleId = (String) response.get("sub");
            String email = (String) response.get("email");
            String name = (String) response.get("name");
            String picture = (String) response.get("picture");
            Boolean verifiedEmail = (Boolean) response.get("email_verified");

            if (verifiedEmail == null || !verifiedEmail) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Email not verified by Google"));
            }

            // Check if user exists by email
            User user = userService.findByEmail(email).orElse(null);

            if (user == null) {
                // Create new user from Google info
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setAvatarUrl(picture);
                user.setRole(Role.USER);
                user.setEnabled(true);
                user.setVerified(true);
                user.setProvider("google");
                user.setProviderId(googleId);
                // Generate a random password (user won't use it)
                user.setPassword(passwordEncoder.encode(generateResetToken()));

                userService.createUser(user);
                log.info("Created new user from Google OAuth: {}", email);
            } else {
                // Update existing user if they're logging in with Google for the first time
                if (!"google".equals(user.getProvider())) {
                    user.setProvider("google");
                    user.setProviderId(googleId);
                }
                if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
                    user.setAvatarUrl(picture);
                }
                userService.updateUser(user);
                log.info("Existing user logged in with Google: {}", email);
            }

            // Check if user is enabled
            if (!user.getEnabled()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Account is disabled"));
            }

            // Generate JWT token for the user
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String jwt = jwtUtils.generateTokenFromUsername(userDetails);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new LoginResponse(jwt, user.getEmail(), user.getName(),
                    roles, user.getAvatarUrl(), user.getId(), user.getCreatedAt()));

        } catch (Exception e) {
            log.error("Google login error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Failed to authenticate with Google"));
        }
    }
}
