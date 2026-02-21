package com.dahtracker.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// 22-2-2025: change login signup feature - DTO for resending verification email

@Data
public class ResendVerificationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
