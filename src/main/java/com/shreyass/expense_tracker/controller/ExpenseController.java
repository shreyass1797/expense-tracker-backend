package com.shreyass.expense_tracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shreyass.expense_tracker.dto.CategorySummary;
import com.shreyass.expense_tracker.dto.ExpenseRequest;
import com.shreyass.expense_tracker.model.Expense;
import com.shreyass.expense_tracker.service.ExpenseService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<Expense> addExpense(@RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.addExpense(request));
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getMyExpenses() {
        return ResponseEntity.ok(expenseService.getMyExpenses());
    }

    @GetMapping("/summary")
    public ResponseEntity<List<CategorySummary>> getCategorySummary() {
        return ResponseEntity.ok(expenseService.getCategorySummary());
    }
}