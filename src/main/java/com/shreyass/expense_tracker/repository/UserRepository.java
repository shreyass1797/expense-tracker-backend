package com.shreyass.expense_tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shreyass.expense_tracker.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // We absolutely need this for the JWT Login process later!
    // Spring Boot automatically writes the SQL query for this just based on the method name.
    Optional<User> findByUsername(String username);
    
    // Checks if a username is already taken during registration
    boolean existsByUsername(String username);
}