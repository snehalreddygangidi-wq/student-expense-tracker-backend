package com.example.expensetracker.service;

import com.example.expensetracker.dto.DashboardResponse;
import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense addExpense(ExpenseRequest request, User user) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Expense title is required.");
        }
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required.");
        }

        Expense expense = new Expense();
        expense.setTitle(request.getTitle().trim());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory().trim());
        expense.setExpenseDate(
            request.getExpenseDate() == null ? LocalDate.now() : request.getExpenseDate()
        );
        expense.setDescription(
            request.getDescription() == null ? "" : request.getDescription().trim()
        );
        expense.setUser(user);

        return expenseRepository.save(expense);
    }

    public List<Expense> getExpenses(User user) {
        return expenseRepository.findByUserIdOrderByExpenseDateDescIdDesc(user.getId());
    }

    public void deleteExpense(Long id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found."));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You cannot delete this expense.");
        }

        expenseRepository.delete(expense);
    }

    public DashboardResponse dashboard(User user) {
        double total = expenseRepository.totalByUser(user.getId());

        YearMonth month = YearMonth.now();
        double monthlyTotal = expenseRepository.totalBetween(
            user.getId(),
            month.atDay(1),
            month.atEndOfMonth()
        );

        long count = expenseRepository.countByUserId(user.getId());

        Map<String, Double> categories = new LinkedHashMap<>();
        for (Object[] row : expenseRepository.totalsByCategory(user.getId())) {
            categories.put((String) row[0], ((Number) row[1]).doubleValue());
        }

        return new DashboardResponse(total, monthlyTotal, count, categories);
    }
}
