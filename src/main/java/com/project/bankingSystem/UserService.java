package com.project.bankingSystem;

import com.project.bankingSystem.models.User;
import com.project.bankingSystem.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public User createUser(User user){
        return userRepository.save(user);
    }

    public User getUser(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }


    public void updateBalance(User user) {
        userRepository.save(user);
    }
}
