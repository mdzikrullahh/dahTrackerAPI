package com.dahtracker.api.service;

import com.dahtracker.api.model.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetService {
    Budget createBudget(Budget budget);
    Optional<Budget> findById(Long id);
    List<Budget> findByUserIdAndMonth(Long userId, Integer month, Integer year);
    Optional<Budget> findByUserIdAndCategoryAndMonth(Long userId, Long categoryId, Integer month, Integer year);
    Budget updateBudget(Budget budget);
    void deleteBudget(Long id);
}
