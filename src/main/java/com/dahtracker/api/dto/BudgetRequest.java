package com.dahtracker.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetRequest {
    private Long categoryId;
    private BigDecimal budgetLimit;
    private Integer month;
    private Integer year;
}
