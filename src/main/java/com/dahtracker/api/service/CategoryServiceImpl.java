package com.dahtracker.api.service;

import com.dahtracker.api.model.Category;
import com.dahtracker.api.model.Budget;
import com.dahtracker.api.repository.CategoryRepository;
import com.dahtracker.api.repository.BudgetRepository;
import com.dahtracker.api.repository.ExpenseRepository;
import com.dahtracker.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> findByUserId(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    @Override
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // 28-02-2026: fix issue unable to delete category due to constraint table
        // 1) delete all expenses associated with this category
        expenseRepository.deleteByCategoryId(id);
        // 2) delete all budgets associated with this category
        budgetRepository.deleteByCategoryId(id);
        // 3) delete the category
        categoryRepository.deleteById(id);
    }
}
