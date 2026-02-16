package com.dahtracker.api.service;

import com.dahtracker.api.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(Category category);
    Optional<Category> findById(Long id);
    List<Category> findByUserId(Long userId);
    Category updateCategory(Category category);
    void deleteCategory(Long id);
}
