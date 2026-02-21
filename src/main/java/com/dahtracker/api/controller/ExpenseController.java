package com.dahtracker.api.controller;

import com.dahtracker.api.model.*;
import com.dahtracker.api.dto.*;
import com.dahtracker.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CardService cardService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllExpenses(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ExpenseResponse> expenses = expenseService.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExpense(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = expenseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        return ResponseEntity.ok(toResponse(expense));
    }

    @GetMapping("/by-date")
    public ResponseEntity<?> getExpensesByDate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ExpenseResponse> expenses = expenseService.findByUserIdAndDate(user.getId(), date).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<?> getExpensesByDateRange(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ExpenseResponse> expenses = expenseService.findByUserIdAndDateRange(user.getId(), startDate, endDate).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/by-card/{cardId}")
    public ResponseEntity<?> getExpensesByCard(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cardId) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ExpenseResponse> expenses = expenseService.findByUserIdAndCardId(user.getId(), cardId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<?> getExpensesByCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long categoryId) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ExpenseResponse> expenses = expenseService.findByUserIdAndCategoryId(user.getId(), categoryId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/summary/daily")
    public ResponseEntity<?> getDailySummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal total = expenseService.getTotalByDate(user.getId(), date);

        return ResponseEntity.ok(new SummaryResponse(date.toString(), total));
    }

    @GetMapping("/summary/monthly")
    public ResponseEntity<?> getMonthlySummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer month,
            @RequestParam Integer year) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal total = expenseService.getTotalByMonth(user.getId(), month, year);

        return ResponseEntity.ok(new SummaryResponse(month + "/" + year, total));
    }

    @GetMapping("/summary/by-card")
    public ResponseEntity<?> getCardMonthlySummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long cardId,
            @RequestParam Integer month,
            @RequestParam Integer year) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal total = expenseService.getTotalByCardAndMonth(user.getId(), cardId, month, year);

        return ResponseEntity.ok(new SummaryResponse("Card " + cardId + " - " + month + "/" + year, total));
    }

    @PostMapping
    public ResponseEntity<?> createExpense(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ExpenseRequest request) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryService.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
        }

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setCategory(category);
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setPaymentType(request.getPaymentType());
        expense.setExpenseDate(request.getExpenseDate());

        // Set card if payment type is CARD
        if (request.getPaymentType() == Expense.PaymentType.CARD) {
            if (request.getCardId() == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Card is required for CARD payment type"));
            }
            Card card = cardService.findById(request.getCardId())
                    .orElseThrow(() -> new RuntimeException("Card not found"));

            if (!card.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Card not found"));
            }
            expense.setCard(card);
        }

        Expense savedExpense = expenseService.createExpense(expense);

        return ResponseEntity.ok(toResponse(savedExpense));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody ExpenseRequest request) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = expenseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        if (request.getCategoryId() != null) {
            Category category = categoryService.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            expense.setCategory(category);
        }

        if (request.getAmount() != null) expense.setAmount(request.getAmount());
        if (request.getDescription() != null) expense.setDescription(request.getDescription());
        if (request.getPaymentType() != null) expense.setPaymentType(request.getPaymentType());
        if (request.getExpenseDate() != null) expense.setExpenseDate(request.getExpenseDate());

        if (request.getCardId() != null) {
            Card card = cardService.findById(request.getCardId())
                    .orElseThrow(() -> new RuntimeException("Card not found"));
            expense.setCard(card);
        } else if (request.getPaymentType() != null && request.getPaymentType() != Expense.PaymentType.CARD) {
            expense.setCard(null);
        }

        Expense updatedExpense = expenseService.updateExpense(expense);

        return ResponseEntity.ok(toResponse(updatedExpense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = expenseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Unauthorized"));
        }

        expenseService.deleteExpense(id);

        return ResponseEntity.ok(new MessageResponse("Expense deleted successfully"));
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getCard() != null ? expense.getCard().getId() : null,
                expense.getCard() != null ? expense.getCard().getName() : null,
                expense.getCard() != null ? expense.getCard().getType().name() : null,
                expense.getCategory().getId(),
                expense.getCategory().getName(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getPaymentType().name(),
                expense.getExpenseDate(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}
