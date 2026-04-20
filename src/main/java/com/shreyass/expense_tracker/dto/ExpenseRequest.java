package com.shreyass.expense_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.shreyass.expense_tracker.model.ExpenseCategory;

import lombok.Data;

@Data
public class ExpenseRequest {
    private BigDecimal amount;
    private String description;
    private LocalDate expenseDate;
    private ExpenseCategory category;
}