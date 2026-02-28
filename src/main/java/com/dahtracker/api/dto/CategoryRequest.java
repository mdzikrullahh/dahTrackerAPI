package com.dahtracker.api.dto;

import lombok.Data;

@Data
public class CategoryRequest {
    private String name;
    private String color;
    private String icon;
    private Integer activeMonth;
    private Integer activeYear;
}
