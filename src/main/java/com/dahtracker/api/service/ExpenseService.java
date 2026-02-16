package com.dahtracker.api.service;

import com.dahtracker.api.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseService {
    Expense createExpense(Expense expense);
    Optional<Expense> findById(Long id);
    List<Expense> findByUserId(Long userId);
    List<Expense> findByUserIdAndCardId(Long userId, Long cardId);
    List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId);
    List<Expense> findByUserIdAndDate(Long userId, LocalDate date);
    List<Expense> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    BigDecimal getTotalByCategoryAndMonth(Long userId, Long categoryId, Integer month, Integer year);
    BigDecimal getTotalByCardAndMonth(Long userId, Long cardId, Integer month, Integer year);
    BigDecimal getTotalByDate(Long userId, LocalDate date);
    BigDecimal getTotalByMonth(Long userId, Integer month, Integer year);
    Expense updateExpense(Expense expense);
    void deleteExpense(Long id);
}
