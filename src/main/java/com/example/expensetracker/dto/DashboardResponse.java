package com.example.expensetracker.dto;

import java.util.Map;

public class DashboardResponse {
    private double total;
    private double monthlyTotal;
    private long expenseCount;
    private Map<String, Double> categoryTotals;

    public DashboardResponse(double total, double monthlyTotal, long expenseCount,
                             Map<String, Double> categoryTotals) {
        this.total = total;
        this.monthlyTotal = monthlyTotal;
        this.expenseCount = expenseCount;
        this.categoryTotals = categoryTotals;
    }

    public double getTotal() {
        return total;
    }

    public double getMonthlyTotal() {
        return monthlyTotal;
    }

    public long getExpenseCount() {
        return expenseCount;
    }

    public Map<String, Double> getCategoryTotals() {
        return categoryTotals;
    }
}
