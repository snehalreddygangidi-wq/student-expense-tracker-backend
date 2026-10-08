package com.example.expensetracker.controller;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.UserRepository;
import com.example.expensetracker.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseService expenseService, UserRepository userRepository) {
        this.expenseService = expenseService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody ExpenseRequest request,
                                        Authentication authentication) {
        try {
            User user = getLoggedInUser(authentication);
            Expense expense = expenseService.addExpense(request, user);
            return ResponseEntity.ok(toResponse(expense));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getExpenses(Authentication authentication) {
        try {
            User user = getLoggedInUser(authentication);
            return ResponseEntity.ok(
                expenseService.getExpenses(user).stream().map(this::toResponse).toList()
            );
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id,
                                           Authentication authentication) {
        try {
            User user = getLoggedInUser(authentication);
            expenseService.deleteExpense(id, user);
            return ResponseEntity.ok(Map.of("message", "Expense deleted."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private User getLoggedInUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Please login first.");
        }

        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new SecurityException("User authentication is invalid."));
    }

    private Map<String, Object> toResponse(Expense expense) {
        return Map.of(
            "id", expense.getId(),
            "title", expense.getTitle(),
            "amount", expense.getAmount(),
            "category", expense.getCategory(),
            "expenseDate", expense.getExpenseDate().toString(),
            "description", expense.getDescription() == null ? "" : expense.getDescription()
        );
    }
}
