package com.dahtracker.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// 22-2-2025: change login signup feature - DTO for Google OAuth login

@Data
public class GoogleLoginRequest {

    @NotBlank(message = "Google ID token is required")
    private String idToken;

    private String accessToken;
}
