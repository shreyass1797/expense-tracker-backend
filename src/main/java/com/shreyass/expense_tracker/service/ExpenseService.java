package com.shreyass.expense_tracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shreyass.expense_tracker.dto.ExpenseRequest;
import com.shreyass.expense_tracker.model.Expense;
import com.shreyass.expense_tracker.model.User;
import com.shreyass.expense_tracker.repository.ExpenseRepository;
import com.shreyass.expense_tracker.repository.UserRepository;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    // Helper method to magically find out who is making the request
    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Expense addExpense(ExpenseRequest request) {
        User user = getAuthenticatedUser();

        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setCategory(request.getCategory());
        
        // Securely link the expense to the logged-in user
        expense.setUser(user);

        return expenseRepository.save(expense);
    }

    public List<Expense> getMyExpenses() {
        User user = getAuthenticatedUser();
        // Return only the expenses belonging to this specific user
        return expenseRepository.findByUser_Id(user.getId());
    }
}