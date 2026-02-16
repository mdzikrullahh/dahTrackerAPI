package com.dahtracker.api.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String avatarUrl;
}
