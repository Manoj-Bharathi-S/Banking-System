package com.project.bankingSystem;

import com.project.bankingSystem.models.Payment;
import com.project.bankingSystem.models.Transaction;
import com.project.bankingSystem.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class BankingService {
    @Autowired
    private UserService userService;
    @Autowired TransactionService transactionService;

    public Transaction pay(Payment payment) {
        User sender = userService.getUser(payment.getFrom_id());
        User receiver = userService.getUser(payment.getTo_id());

        if(sender == null || receiver == null){
            return null;
        }


        Double amount = payment.getAmount();
        if(sender.getBalance() < amount){
            return null;
        }
        receiver.setBalance(receiver.getBalance() + amount);
        sender.setBalance(sender.getBalance() - amount);

        Transaction txn1 = Transaction.builder()
                .user_id(payment.getFrom_id())
                .debit(payment.getAmount())
                .credit(0.00)
                .balance(sender.getBalance())
                .createdAt(LocalDateTime.now())
                .build();

        Transaction txn2 = Transaction.builder()
                .user_id(payment.getTo_id())
                .debit(0.00)
                .credit(payment.getAmount())
                .balance(receiver.getBalance())
                .createdAt(LocalDateTime.now())
                .build();

        transactionService.createTransaction(txn2);
        return txn1;

    }

    public Transaction withdraw(Transaction transaction) {
        User user = userService.getUser(transaction.getUser_id());
//                .orElseThrow(() -> new RuntimeException("Account profile not found"));

        if(user.getBalance() < transaction.getDebit()){
            return null;
        }
        Double curr_balance = user.getBalance() - transaction.getDebit();

        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setBalance(curr_balance);
        transaction.setCredit(0.00);
        user.setBalance(curr_balance);
        userService.updateBalance(user);
        return transaction;
    }

    public Transaction deposit(Transaction transaction) {
        User user = userService.getUser(transaction.getUser_id());
        Double curr_balance = user.getBalance() + transaction.getCredit();

        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setBalance(curr_balance);
        transaction.setDebit(0.00);

        user.setBalance(curr_balance);
//        transaction.setUser(user);
        userService.updateBalance(user);
        return transaction;
    }
}
