package com.dahtracker.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

// 22-2-2025: change login signup feature - DTO for password reset

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d).*$",
        message = "Password must contain at least one uppercase letter and one number (min 6 characters)"
    )
    private String newPassword;
}
