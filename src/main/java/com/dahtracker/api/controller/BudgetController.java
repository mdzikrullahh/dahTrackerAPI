package com.dahtracker.api.controller;

import com.dahtracker.api.model.Budget;
import com.dahtracker.api.model.Category;
import com.dahtracker.api.model.User;
import com.dahtracker.api.dto.BudgetRequest;
import com.dahtracker.api.dto.BudgetResponse;
import com.dahtracker.api.dto.MessageResponse;
import com.dahtracker.api.service.BudgetService;
import com.dahtracker.api.service.CategoryService;
import com.dahtracker.api.service.ExpenseService;
import com.dahtracker.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final ExpenseService expenseService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getBudgetsByMonth(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer month,
            @RequestParam Integer year) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<BudgetResponse> budgets = budgetService.findByUserIdAndMonth(user.getId(), month, year).stream()
                .map(budget -> toResponseWithSpent(budget, user.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Budget budget = budgetService.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (!budget.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        return ResponseEntity.ok(toResponseWithSpent(budget, user.getId()));
    }

    @PostMapping
    public ResponseEntity<?> createBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BudgetRequest request) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryService.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
        }

        // Check if budget already exists for this category/month/year
        if (budgetService.findByUserIdAndCategoryAndMonth(user.getId(), request.getCategoryId(), request.getMonth(), request.getYear()).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Budget already exists for this category and month"));
        }

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setBudgetLimit(request.getBudgetLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        Budget savedBudget = budgetService.createBudget(budget);

        return ResponseEntity.ok(toResponseWithSpent(savedBudget, user.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody BudgetRequest request) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Budget budget = budgetService.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (!budget.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        if (request.getBudgetLimit() != null) budget.setBudgetLimit(request.getBudgetLimit());

        Budget updatedBudget = budgetService.updateBudget(budget);

        return ResponseEntity.ok(toResponseWithSpent(updatedBudget, user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Budget budget = budgetService.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (!budget.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        budgetService.deleteBudget(id);

        return ResponseEntity.ok(new MessageResponse("Budget deleted successfully"));
    }

    private BudgetResponse toResponseWithSpent(Budget budget, Long userId) {
        BigDecimal spent = expenseService.getTotalByCategoryAndMonth(
                userId,
                budget.getCategory().getId(),
                budget.getMonth(),
                budget.getYear()
        );

        return new BudgetResponse(
                budget.getId(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                budget.getBudgetLimit(),
                spent,
                budget.getMonth(),
                budget.getYear(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }
}
