package com.example.expensetracker.repository;

import com.example.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByExpenseDateDescIdDesc(Long userId);

    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.user.id = :userId")
    double totalByUser(@Param("userId") Long userId);

    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.user.id = :userId and e.expenseDate between :start and :end")
    double totalBetween(@Param("userId") Long userId,
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

    long countByUserId(Long userId);

    @Query("select e.category, coalesce(sum(e.amount), 0) from Expense e where e.user.id = :userId group by e.category")
    List<Object[]> totalsByCategory(@Param("userId") Long userId);
}
