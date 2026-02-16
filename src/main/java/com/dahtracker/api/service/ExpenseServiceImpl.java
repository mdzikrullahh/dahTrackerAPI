package com.dahtracker.api.service;

import com.dahtracker.api.model.Expense;
import com.dahtracker.api.repository.ExpenseRepository;
import com.dahtracker.api.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Override
    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public Optional<Expense> findById(Long id) {
        return expenseRepository.findById(id);
    }

    @Override
    public List<Expense> findByUserId(Long userId) {
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(userId);
    }

    @Override
    public List<Expense> findByUserIdAndCardId(Long userId, Long cardId) {
        return expenseRepository.findByUserIdAndCardId(userId, cardId);
    }

    @Override
    public List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId) {
        return expenseRepository.findByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    public List<Expense> findByUserIdAndDate(Long userId, LocalDate date) {
        return expenseRepository.findByUserIdAndExpenseDate(userId, date);
    }

    @Override
    public List<Expense> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndExpenseDateBetween(userId, startDate, endDate);
    }

    @Override
    public BigDecimal getTotalByCategoryAndMonth(Long userId, Long categoryId, Integer month, Integer year) {
        BigDecimal total = expenseRepository.getTotalByCategoryAndMonth(userId, categoryId, month, year);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalByCardAndMonth(Long userId, Long cardId, Integer month, Integer year) {
        BigDecimal total = expenseRepository.getTotalByCardAndMonth(userId, cardId, month, year);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalByDate(Long userId, LocalDate date) {
        BigDecimal total = expenseRepository.getTotalByDate(userId, date);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalByMonth(Long userId, Integer month, Integer year) {
        BigDecimal total = expenseRepository.getTotalByMonth(userId, month, year);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Expense updateExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }
}
