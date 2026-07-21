package com.project.bankingSystem;


import com.project.bankingSystem.models.Payment;
import com.project.bankingSystem.models.Transaction;
import com.project.bankingSystem.models.User;
import com.project.bankingSystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class BankingController {

    @Autowired
    private UserService userService;
    @Autowired
    private BankingService bankingService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserRepository userRepository;

    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/profile")
    ResponseEntity<?> homePage(Principal principal){
        String username = principal.getName();
        var ownerOpt = userRepository.findByUsername(username);
        if(ownerOpt.isEmpty()){
            return new ResponseEntity<>("Access Denied: Unauthorized User.", HttpStatus.FORBIDDEN);
        }
        User user = ownerOpt.get();
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("ac", user.getAc());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("balance", user.getBalance());
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }


    @PostMapping("/deposit")
    ResponseEntity<Object> deposit(@RequestBody Payment payment, Principal principal){
        String username = principal.getName();
        var ownerOpt = userRepository.findByUsername(username);
        if(ownerOpt.isEmpty()){
            return new ResponseEntity<>("Access Denied: Unauthorized User.", HttpStatus.FORBIDDEN);
        }
        User owner = ownerOpt.get();
        payment.setTo_id(owner.getId());

        if(payment.getAmount() == null || payment.getAmount() <= 0.0){
            return new ResponseEntity<>("Access Denied: The amount must be greater than 0.", HttpStatus.BAD_REQUEST);
        }
        Transaction transaction = bankingService.deposit(payment);
        if(transaction == null){
            return new ResponseEntity<>("Deposit failed.", HttpStatus.BAD_REQUEST);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", transaction.getId());
        response.put("amount", transaction.getAmount());
        response.put("type", transaction.getType());
        response.put("balanceAfterTransaction", transaction.getBalanceAfterTransaction());
        response.put("user", owner.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/withdraw")
    ResponseEntity<Object> withdraw(@RequestBody Payment payment, Principal principal){
        String username = principal.getName();
        var ownerOpt = userRepository.findByUsername(username);
        if(ownerOpt.isEmpty()){
            return new ResponseEntity<>("Access Denied: Unauthorized User.", HttpStatus.FORBIDDEN);
        }
        User owner = ownerOpt.get();
        payment.setFrom_id(owner.getId());

        if(payment.getAmount() == null || payment.getAmount() <= 0.0){
            return new ResponseEntity<>("Access Denied: The amount must be greater than 0.", HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = bankingService.withdraw(payment);
        if(transaction == null){
            return new ResponseEntity<>("Access Denied: You do not have sufficient funds to withdraw.", HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("id", transaction.getId());
        response.put("amount", transaction.getAmount());
        response.put("type", transaction.getType());
        response.put("balanceAfterTransaction", transaction.getBalanceAfterTransaction());
        response.put("user", owner.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/transfer")
    ResponseEntity<?> transfer(@RequestBody Payment payment, Principal principal){
        String username = principal.getName();
        var ownerOpt = userRepository.findByUsername(username);
        if(ownerOpt.isEmpty()){
            return new ResponseEntity<>("Access Denied: Unauthorized User.", HttpStatus.FORBIDDEN);
        }
        User owner = ownerOpt.get();
        payment.setFrom_id(owner.getId());

        Long fromId = owner.getId();
        Long toId = payment.getTo_id();

        if(toId == null || Objects.equals(fromId, toId)){
            return new ResponseEntity<>("Access Denied: Invalid Recipient.", HttpStatus.BAD_REQUEST);
        }
        Double amount = payment.getAmount();
        if(amount == null || amount <= 0.0){
            return new ResponseEntity<>("Access Denied: The amount must be greater than 0.", HttpStatus.BAD_REQUEST);
        }

        if(owner.getBalance() < amount){
            return new ResponseEntity<>("Access Denied: You do not have sufficient funds to send.", HttpStatus.BAD_REQUEST);
        }
        Transaction transaction = bankingService.transfer(payment);
        if(transaction == null){
            return new ResponseEntity<>("Access Denied: You do not have sufficient funds or invalid recipient.", HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", transaction.getId());
        response.put("amount", transaction.getAmount());
        response.put("type", transaction.getType());
        response.put("balanceAfterTransaction", transaction.getBalanceAfterTransaction());
        response.put("user", fromId);
        response.put("relatedAccountId", toId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/mybalance")
    Double getMyBalance(Principal principal){
        String username = principal.getName();
        var userOpt = userRepository.findByUsername(username);
        if(userOpt.isEmpty()){
            return 0.0;
        }
        Long id = userOpt.get().getId();

        return bankingService.calculateCurrentBalance(id);
    }



}
