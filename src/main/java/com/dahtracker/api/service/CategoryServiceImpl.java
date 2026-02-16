package com.dahtracker.api.service;

import com.dahtracker.api.model.Category;
import com.dahtracker.api.model.Budget;
import com.dahtracker.api.repository.CategoryRepository;
import com.dahtracker.api.repository.BudgetRepository;
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
        // First, delete all budgets associated with this category
        budgetRepository.deleteByCategoryId(id);
        // Then delete the category
        categoryRepository.deleteById(id);
    }
}
