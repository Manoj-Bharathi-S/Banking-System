package com.project.bankingSystem;


import com.project.bankingSystem.models.Payment;
import com.project.bankingSystem.models.Transaction;
import com.project.bankingSystem.models.User;
import com.project.bankingSystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.security.Principal;
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
        ObjectNode jsonNode = mapper.valueToTree(user);
//        jsonNode.remove("id");
        jsonNode.remove("password");
        jsonNode.remove("balance");
        jsonNode.remove("version");
        return new ResponseEntity<>(jsonNode,HttpStatus.OK);
    }


    @PostMapping("/deposit")
    ResponseEntity<Object> deposit(@RequestBody Payment payment,Principal principal){

        Long toId = payment.getTo_id();

        String username = principal.getName();
        var ownerOpt = userRepository.findByUsername(username);
        if(ownerOpt.isEmpty()){
            return new ResponseEntity<>("Access Denied: Unauthorized User.", HttpStatus.FORBIDDEN);
        }
        User owner = ownerOpt.get();
        if (!owner.getId().equals(toId)) {
            return new ResponseEntity<>("Access Denied: Unauthorized User.", HttpStatus.FORBIDDEN);
        }
        if(payment.getAmount() == 0.0){
            return new ResponseEntity<>("Access Denied: The amount must be greater than 0.", HttpStatus.FORBIDDEN);
        }
        Transaction transaction = bankingService.deposit(payment);
        
        ObjectNode jsonNode = mapper.valueToTree(transaction);
        jsonNode.put("user", toId);
        return new ResponseEntity<>(jsonNode,HttpStatus.OK);

    }

    @PostMapping("/withdraw")
    ResponseEntity<Object> withdraw(@RequestBody Payment payment,Principal principal){
        Long fromId = payment.getFrom_id();
        String username = principal.getName();
        var ownerOpt = userRepository.findByUsername(username);
        User owner = ownerOpt.get();
        if (!owner.getId().equals(fromId)) {
            return new ResponseEntity<>("Access Denied: Unauthorized User.", HttpStatus.FORBIDDEN);
        }

        Transaction transaction = bankingService.withdraw(payment);
        if(transaction == null){
            return new ResponseEntity<>("Access Denied: You do not have sufficient funds to withdraw.", HttpStatus.FORBIDDEN);
        }
        ObjectNode jsonNode = mapper.valueToTree(transaction);
        jsonNode.put("user", fromId);
        return new ResponseEntity<>(jsonNode,HttpStatus.OK);
    }

    @PostMapping("/transfer")
    ResponseEntity<?> transfer(@RequestBody Payment payment, Principal principal){
//        Long fromId, Long toId, Double amount
        String username = principal.getName();

        Long fromId = payment.getFrom_id();
        Long toId = payment.getTo_id();
        if(Objects.equals(fromId, toId)){
            return new ResponseEntity<>("Access Denied: Invalid Transaction.", HttpStatus.FORBIDDEN);
        }
        Double amount = payment.getAmount();
        var ownerOpt = userRepository.findByUsername(username);
        User owner = ownerOpt.get();
        if (!owner.getId().equals(fromId)) {
            return new ResponseEntity<>("Access Denied: Unauthorized User.", HttpStatus.FORBIDDEN);
        }
        if(owner.getBalance() < amount){
            return new ResponseEntity<>("Access Denied: You do not have sufficient funds to send.", HttpStatus.FORBIDDEN);
        }
        Transaction transaction = bankingService.transfer(payment);
        if(transaction == null){
            return new ResponseEntity<>("Access Denied: You do not have sufficient funds to send.", HttpStatus.FORBIDDEN);
        }
        transaction.setUser(null);


// 2. Edit the JSON directly

        ObjectNode jsonNode = mapper.valueToTree(transaction);
        jsonNode.put("user", fromId);
        return new ResponseEntity<>(jsonNode,HttpStatus.OK);
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
