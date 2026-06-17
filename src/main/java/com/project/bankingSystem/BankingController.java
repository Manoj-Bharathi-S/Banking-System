package com.project.bankingSystem;


import com.project.bankingSystem.models.Payment;
import com.project.bankingSystem.models.User;
import com.project.bankingSystem.models.Transaction;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class BankingController {

    @Autowired
    private UserService userService;
    @Autowired
    private BankingService bankingService;
    @Autowired
    private TransactionService transactionService;



    @GetMapping("/profile")
    ResponseEntity<User> homePage(@RequestBody Map<String, Long> payload){
        return new ResponseEntity<>(userService.getUser(payload.get("id")),HttpStatus.OK);
    }

    @PostMapping("/pay")
    ResponseEntity<Transaction> pay(@RequestBody Payment payment){

        Transaction txn1 = bankingService.pay(payment);
        if(txn1 == null){
            return new ResponseEntity<>((HttpHeaders) null,HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(transactionService.createTransaction(txn1),HttpStatus.OK);

    }

    @PostMapping("/deposit")
    ResponseEntity<Transaction> deposit(@RequestBody Transaction transaction){
        bankingService.deposit(transaction);
        return new ResponseEntity<>(transactionService.createTransaction(transaction),HttpStatus.OK);

    }

    @PostMapping("/withdraw")
    ResponseEntity<Object> withdraw(@RequestBody Transaction transaction){

        Transaction t = bankingService.withdraw(transaction);
        if(t == null){
            return new ResponseEntity<>("Access Denied: You do not have sufficient funds to withdraw.", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(transactionService.createTransaction(t),HttpStatus.OK);

    }


}
