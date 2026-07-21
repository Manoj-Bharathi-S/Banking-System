package com.project.bankingSystem;

import com.project.bankingSystem.models.Transaction;
import com.project.bankingSystem.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
