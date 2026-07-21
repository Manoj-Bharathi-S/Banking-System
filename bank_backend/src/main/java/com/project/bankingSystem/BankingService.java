package com.project.bankingSystem;

import com.project.bankingSystem.models.*;
import com.project.bankingSystem.repositories.TransactionRepository;
import com.project.bankingSystem.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class BankingService {
    @Autowired
    private UserService userService;
    @Autowired TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;



    @Transactional
    public Transaction withdraw(Payment payment) {
        var userOptional = userRepository.findByIdForUpdate(payment.getFrom_id());
        if(userOptional.isEmpty()){
            return null;
        }
        User user = userOptional.get();

        if(user.getBalance() < payment.getAmount()){
            return null;
        }
        Double curr_balance = user.getBalance() - payment.getAmount();
        Double amount = payment.getAmount();
        Transaction tx = Transaction.builder()
                .user(user)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .balanceAfterTransaction(curr_balance)
                .createdAt(LocalDateTime.now())
                .relatedAccountId(user.getId())
                .build();

        user.setBalance(curr_balance);
        userService.updateBalance(user);
        return transactionRepository.save(tx);

    }



    @Transactional
    public Transaction deposit(Payment payment) {
        var userOptional = userRepository.findByIdForUpdate(payment.getTo_id());
        if(userOptional.isEmpty()){
            return null;
        }
        User user = userOptional.get();

        Double curr_balance = user.getBalance() + payment.getAmount();

        Transaction tx = Transaction.builder()
                .user(user)
                .amount(payment.getAmount())
                .type(TransactionType.DEPOSIT)
                .balanceAfterTransaction(curr_balance)
                .createdAt(LocalDateTime.now())
                .relatedAccountId(user.getId())
                .build();

        user.setBalance(curr_balance);
        userService.updateBalance(user);
        return transactionRepository.save(tx);
    }

    public Double calculateCurrentBalance(Long userId) {
        return userRepository.findById(userId)
                .map(User::getBalance)
                .orElse(0.0);
    }   

    @Transactional
    public Transaction transfer(Payment payment) {
        // 1. Lock both rows to prevent race conditions
        Long fromId = payment.getFrom_id();
        Long toId = payment.getTo_id();
        Double amount = payment.getAmount();
        var senderOptional = userRepository.findByIdForUpdate(fromId);
        var receiverOptional = userRepository.findByIdForUpdate(toId);

        if(senderOptional.isEmpty() || receiverOptional.isEmpty()){
            return null;
        }
        User sender = senderOptional.get();
        User receiver = receiverOptional.get();

        // 2. Execute logic
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        // 3. Create two ledger entries
        Transaction tx1 = Transaction.builder()
                .user(sender)
                .amount(amount)
                .type(TransactionType.TRANSFER_OUT)
                .balanceAfterTransaction(sender.getBalance())
                .createdAt(LocalDateTime.now())
                .relatedAccountId(receiver.getId())
                .build();

        Transaction tx2 = Transaction.builder()
                .user(receiver)
                .amount(amount)
                .type(TransactionType.TRANSFER_IN)
                .balanceAfterTransaction(receiver.getBalance())
                .createdAt(LocalDateTime.now())
                .relatedAccountId(sender.getId())
                .build();
        transactionRepository.save(tx2);
        return transactionRepository.save(tx1);
    }
}
