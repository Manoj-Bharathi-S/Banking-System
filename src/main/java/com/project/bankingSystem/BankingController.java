package com.project.bankingSystem;


import com.project.bankingSystem.models.Payment;
import com.project.bankingSystem.models.User;
import com.project.bankingSystem.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class BankingController {
    @GetMapping("/profile")
    ResponseEntity<User> homePage(@RequestBody Map<String, Long> payload){
        return new ResponseEntity<>(userService.getUser(payload.get("id")),HttpStatus.OK);
    }


    @Autowired
    private UserService userService;
    @PostMapping("/create")
    ResponseEntity<User> createUser(@RequestBody User body){
        body.setBalance(0.00);
        return new ResponseEntity<>(userService.createUser(body), HttpStatus.CREATED);
    }

    @Autowired
    private UserRepository userRepository;
    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody Map<String,String> body){
        String username = body.get("username");
        String password = body.get("password");

        var userOptional = userRepository.findByUsername(username);
        if(userOptional.isEmpty()){
            return new ResponseEntity<>("User does not Exist", HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        if(!password.equals(user.getPassword())){
            return new ResponseEntity<>("Invalid Password",HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok("Login Successful");

    }

    @Autowired
    private TransactionService transactionService;
    @PostMapping("/pay")
    ResponseEntity<Transaction> pay(@RequestBody Payment payment){

        var sender = userService.getUser(payment.getFrom_id());
        var receiver = userService.getUser(payment.getTo_id());

        if(sender == null || receiver == null){
            return new ResponseEntity<>((HttpHeaders) null,HttpStatus.UNAUTHORIZED);
        }
        Double amount = payment.getAmount();
        if(sender.getBalance() < amount){
            return new ResponseEntity<>((HttpHeaders) null,HttpStatus.UNAUTHORIZED);
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
        return new ResponseEntity<>(transactionService.createTransaction(txn1),HttpStatus.OK);

    }


    @PostMapping("/deposit")
    ResponseEntity<Transaction> deposit(@RequestBody Transaction transaction){

        var user = userService.getUser(transaction.getUser_id());
        Double curr_balance = user.getBalance() + transaction.getCredit();

        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setBalance(curr_balance);
        transaction.setDebit(0.00);

        user.setBalance(curr_balance);
        userService.updateBalance(user);

        return new ResponseEntity<>(transactionService.createTransaction(transaction),HttpStatus.OK);

    }

    @PostMapping("/withdraw")
    ResponseEntity<Transaction> withdraw(@RequestBody Transaction transaction){
        var user = userService.getUser(transaction.getUser_id());

        Double curr_balance = user.getBalance() - transaction.getDebit();

        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setBalance(curr_balance);
        transaction.setCredit(0.00);

        user.setBalance(curr_balance);
        userService.updateBalance(user);

        return new ResponseEntity<>(transactionService.createTransaction(transaction),HttpStatus.OK);

    }


}
