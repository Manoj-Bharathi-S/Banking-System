package com.project.bankingSystem;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.bankingSystem.models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

}
