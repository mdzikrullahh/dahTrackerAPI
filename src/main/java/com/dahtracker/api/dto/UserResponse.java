package com.dahtracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String avatarUrl;
    private String role;
    private LocalDateTime createdAt;
}
