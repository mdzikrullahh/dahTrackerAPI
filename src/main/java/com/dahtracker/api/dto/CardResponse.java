package com.dahtracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CardResponse {
    private Long id;
    private String name;
    private String type;
    private Boolean defaultCard;
    private String color;
    private String holder;
    private Integer seq;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
