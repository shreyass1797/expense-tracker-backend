package com.shreyass.expense_tracker.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private BigDecimal monthlyBudget; // Optional, they can set this later
}