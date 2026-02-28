package com.dahtracker.api.controller;

import com.dahtracker.api.model.Category;
import com.dahtracker.api.model.User;
import com.dahtracker.api.dto.CategoryRequest;
import com.dahtracker.api.dto.CategoryResponse;
import com.dahtracker.api.dto.MessageResponse;
import com.dahtracker.api.service.CategoryService;
import com.dahtracker.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllCategories(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CategoryResponse> categories = categoryService.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        return ResponseEntity.ok(toResponse(category));
    }

    @PostMapping
    public ResponseEntity<?> createCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CategoryRequest request) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = new Category();
        category.setUser(user);
        category.setName(request.getName());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        category.setActiveMonth(request.getActiveMonth());
        category.setActiveYear(request.getActiveYear());

        Category savedCategory = categoryService.createCategory(category);

        return ResponseEntity.ok(toResponse(savedCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        if (request.getName() != null) category.setName(request.getName());
        if (request.getColor() != null) category.setColor(request.getColor());
        if (request.getIcon() != null) category.setIcon(request.getIcon());
        if (request.getActiveMonth() != null) category.setActiveMonth(request.getActiveMonth());
        if (request.getActiveYear() != null) category.setActiveYear(request.getActiveYear());

        Category updatedCategory = categoryService.updateCategory(category);

        return ResponseEntity.ok(toResponse(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        categoryService.deleteCategory(id);

        return ResponseEntity.ok(new MessageResponse("Category deleted successfully"));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getColor(),
                category.getIcon(),
                category.getActiveMonth(),
                category.getActiveYear(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
