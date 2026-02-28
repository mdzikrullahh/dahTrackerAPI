package com.dahtracker.api.repository;

import com.dahtracker.api.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserIdAndExpenseDate(Long userId, LocalDate date);

    List<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);

    List<Expense> findByUserIdAndCardId(Long userId, Long cardId);

    List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
    BigDecimal getTotalByCategoryAndMonth(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.card.id = :cardId AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
    BigDecimal getTotalByCardAndMonth(@Param("userId") Long userId, @Param("cardId") Long cardId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.expenseDate = :date")
    BigDecimal getTotalByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
    BigDecimal getTotalByMonth(@Param("userId") Long userId, @Param("month") Integer month, @Param("year") Integer year);

    void deleteByCardId(Long cardId);

    void deleteByCategoryId(Long categoryId);
}