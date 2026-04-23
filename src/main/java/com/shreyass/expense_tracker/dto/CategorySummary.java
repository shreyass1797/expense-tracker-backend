package com.shreyass.expense_tracker.dto;

import com.shreyass.expense_tracker.model.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor // JPA needs this constructor to inject the SQL results
public class CategorySummary {
    private ExpenseCategory category;
    private BigDecimal totalAmount;
}