package com.dahtracker.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 22-2-2025: change login signup feature - DTO for email verification

@Data
public class VerifyEmailRequest {

    @NotBlank(message = "Email is required")
    @jakarta.validation.constraints.Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "PIN is required")
    @Size(min = 6, max = 6, message = "PIN must be 6 digits")
    private String pin;
}
