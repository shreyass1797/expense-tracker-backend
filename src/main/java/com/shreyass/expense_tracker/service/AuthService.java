package com.shreyass.expense_tracker.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shreyass.expense_tracker.dto.AuthResponse;
import com.shreyass.expense_tracker.dto.LoginRequest;
import com.shreyass.expense_tracker.dto.RegisterRequest;
import com.shreyass.expense_tracker.model.User;
import com.shreyass.expense_tracker.repository.UserRepository;
import com.shreyass.expense_tracker.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        // 1. Check if username is taken
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // 2. Create the new user and hash the password
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Default budget to 0 if they didn't provide one
        user.setMonthlyBudget(request.getMonthlyBudget() != null ? request.getMonthlyBudget() : BigDecimal.ZERO);

        // 3. Save to database
        userRepository.save(user);

        // 4. Generate the JWT so they don't have to log in immediately after registering
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, "User registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        // 1. The security checks the password against the database hash
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. If it matches, generate a JWT and return it
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, "Login successful");
    }
}