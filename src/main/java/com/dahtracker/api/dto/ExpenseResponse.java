package com.dahtracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExpenseResponse {
    private Long id;
    private Long cardId;
    private String cardName;
    private String cardType;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private String description;
    private String paymentType;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
