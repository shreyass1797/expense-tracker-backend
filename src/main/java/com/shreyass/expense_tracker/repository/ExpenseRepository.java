package com.shreyass.expense_tracker.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shreyass.expense_tracker.dto.CategorySummary;
import com.shreyass.expense_tracker.model.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Standard query: "Get all expenses for a specific user"
    List<Expense> findByUser_Id(Long userId);

    @Query("SELECT new com.shreyass.expense_tracker.dto.CategorySummary(e.category, SUM(e.amount)) " +
           "FROM Expense e WHERE e.user.id = :userId GROUP BY e.category")
    List<CategorySummary> getCategorySpendSummary(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalSpendForMonth(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}