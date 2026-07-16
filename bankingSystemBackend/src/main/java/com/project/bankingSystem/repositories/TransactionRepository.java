package com.project.bankingSystem.repositories;

import com.project.bankingSystem.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    // In your TransactionRepository
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findStatementByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId")
    Double sumAllAmountsByUserId(@Param("userId") Long userId);
}