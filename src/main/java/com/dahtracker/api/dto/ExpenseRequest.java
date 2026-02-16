package com.dahtracker.api.dto;

import com.dahtracker.api.model.Expense;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    private Long cardId;
    private Long categoryId;
    private BigDecimal amount;
    private String description;
    private Expense.PaymentType paymentType;
    private LocalDate expenseDate;
}
