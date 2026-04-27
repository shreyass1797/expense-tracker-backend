package com.shreyass.expense_tracker.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.shreyass.expense_tracker.dto.ErrorResponse;

@ControllerAdvice // This tells Spring: "I am the Safety Net"
public class GlobalExceptionHandler {

    // This tells Spring: "If a BudgetExceededException falls, catch it here!"
    @ExceptionHandler(BudgetExceededException.class)
    public ResponseEntity<ErrorResponse> handleBudgetException(BudgetExceededException ex) {
        
        // 1. We build our clean JSON response
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(), // Changes the 500 to a 400!
                "Budget Violation",
                ex.getMessage() // This pulls the text you wrote in the Service
        );

        // 2. We return it 
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Catch everything else!
    // If ANY other weird error happens, catch it here instead of crashing
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}