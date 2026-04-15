package com.shreyass.expense_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shreyass.expense_tracker.model.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Standard query: "Get all expenses for a specific user"
    List<Expense> findByUser_Id(Long userId);

    // We will add our advanced aggregation queries (like Monthly Budgets) here later!
}