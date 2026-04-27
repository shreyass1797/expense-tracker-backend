package com.shreyass.expense_tracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shreyass.expense_tracker.dto.CategorySummary;
import com.shreyass.expense_tracker.dto.ExpenseRequest;
import com.shreyass.expense_tracker.exception.BudgetExceededException;
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
        
        // 1. Only run the check if the user actually set a budget greater than 0
        if (user.getMonthlyBudget() != null && user.getMonthlyBudget().compareTo(BigDecimal.ZERO) > 0) {
            
            // 2. Figure out the 1st and last day of the month based on the expense date
            YearMonth currentMonth = YearMonth.from(request.getExpenseDate());
            LocalDate startDate = currentMonth.atDay(1);
            LocalDate endDate = currentMonth.atEndOfMonth();

            // 3. Ask the database how much they've spent this month
            BigDecimal currentSpend = expenseRepository.getTotalSpendForMonth(user.getId(), startDate, endDate);
            
            // 4. Calculate the projected total
            BigDecimal projectedSpend = currentSpend.add(request.getAmount());

            // 5. If projected > budget, crash the process!
            if (projectedSpend.compareTo(user.getMonthlyBudget()) > 0) {
                throw new BudgetExceededException("Over Budget! Adding this expense exceeds your monthly limit of " + user.getMonthlyBudget());
            }
        }
        
        // -------------------------------

        // If they passed the check (or have no budget), proceed normally
        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setCategory(request.getCategory());
        expense.setUser(user);

        return expenseRepository.save(expense);
    }

    public List<Expense> getMyExpenses() {
        User user = getAuthenticatedUser();
        // Return only the expenses belonging to this specific user
        return expenseRepository.findByUser_Id(user.getId());
    }

    public List<CategorySummary> getCategorySummary() {
        User user = getAuthenticatedUser();
        return expenseRepository.getCategorySpendSummary(user.getId());
    }


}