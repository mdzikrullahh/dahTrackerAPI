package com.dahtracker.api.service;

import com.dahtracker.api.dto.brevo.BrevoEmailRequest;
import com.dahtracker.api.dto.brevo.EmailRecipient;
import com.dahtracker.api.dto.brevo.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// 22-2-2025: change login signup feature - Email service implementation using Brevo API

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.api.url:https://api.brevo.com/v3/smtp/email}")
    private String brevoApiUrl;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendVerificationEmail(String to, String name, String pin) {
        // 22-2-2025: change login signup feature - Log PIN for development testing
        log.info("========================================");
        log.info("EMAIL VERIFICATION PIN FOR: {}", to);
        log.info("PIN: {}", pin);
        log.info("========================================");

        String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <div style="background-color: #4F46E5; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;">
                    <h1 style="color: white; margin: 0;">DahTracker</h1>
                </div>
                <div style="padding: 30px; background-color: #f9fafb; border-radius: 0 0 8px 8px;">
                    <h2 style="color: #1f2937;">Verify Your Email</h2>
                    <p style="color: #4b5563;">Hi %s,</p>
                    <p style="color: #4b5563;">Thank you for registering with DahTracker! Please use the following verification code to complete your registration:</p>
                    <div style="background-color: #e0e7ff; padding: 20px; text-align: center; border-radius: 8px; margin: 20px 0;">
                        <span style="font-size: 32px; font-weight: bold; color: #4F46E5; letter-spacing: 5px;">%s</span>
                    </div>
                    <p style="color: #4b5563;">This code will expire in 15 minutes.</p>
                    <p style="color: #4b5563;">If you didn't create an account with DahTracker, please ignore this email.</p>
                </div>
                <div style="text-align: center; padding: 20px; color: #9ca3af; font-size: 12px;">
                    <p>&copy; 2025 DahTracker. All rights reserved.</p>
                </div>
            </body>
            </html>
            """.formatted(name, pin);

        sendEmail(to, "Verify Your Email Address - DahTracker", htmlContent);
        log.info("Verification email sent to: {}", to);
    }

    @Override
    public void sendPasswordResetEmail(String to, String name, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        // 22-2-2025: change login signup feature - Log reset link for development testing
        log.info("========================================");
        log.info("PASSWORD RESET LINK FOR: {}", to);
        log.info("LINK: {}", resetLink);
        log.info("TOKEN: {}", resetToken);
        log.info("========================================");

        String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <div style="background-color: #4F46E5; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;">
                    <h1 style="color: white; margin: 0;">DahTracker</h1>
                </div>
                <div style="padding: 30px; background-color: #f9fafb; border-radius: 0 0 8px 8px;">
                    <h2 style="color: #1f2937;">Reset Your Password</h2>
                    <p style="color: #4b5563;">Hi %s,</p>
                    <p style="color: #4b5563;">We received a request to reset your password. Click the button below to create a new password:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #4F46E5; color: white; padding: 15px 30px; text-decoration: none; border-radius: 8px; display: inline-block; font-weight: bold;">Reset Password</a>
                    </div>
                    <p style="color: #4b5563;">Or copy and paste this link into your browser:</p>
                    <p style="color: #4b5563; word-break: break-all; font-size: 12px;">%s</p>
                    <p style="color: #4b5563;">This link will expire in 1 hour.</p>
                    <p style="color: #4b5563;">If you didn't request a password reset, please ignore this email.</p>
                </div>
                <div style="text-align: center; padding: 20px; color: #9ca3af; font-size: 12px;">
                    <p>&copy; 2025 DahTracker. All rights reserved.</p>
                </div>
            </body>
            </html>
            """.formatted(name, resetLink, resetLink);

        sendEmail(to, "Reset Your Password - DahTracker", htmlContent);
        log.info("Password reset email sent to: {}", to);
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            BrevoEmailRequest emailRequest = new BrevoEmailRequest();
            emailRequest.setSender(new EmailSender("noreply@dahtracker.com", "DahTracker"));
            emailRequest.setTo(List.of(new EmailRecipient(to)));
            emailRequest.setSubject(subject);
            emailRequest.setHtmlContent(htmlContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            HttpEntity<BrevoEmailRequest> request = new HttpEntity<>(emailRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    brevoApiUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully via Brevo");
            } else {
                log.error("Failed to send email. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            }

        } catch (Exception e) {
            log.error("Error sending email via Brevo: {}", e.getMessage(), e);
            // Don't throw - allow registration to continue even if email fails (for dev)
        }
    }
}
