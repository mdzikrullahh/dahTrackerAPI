package com.dahtracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String color;
    private String icon;
    private Integer activeMonth;
    private Integer activeYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
