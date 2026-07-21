package com.project.bankingSystem;

import com.project.bankingSystem.models.User;
import com.project.bankingSystem.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

//    @Transactional
    public void updateBalance(User user) {
        userRepository.save(user);
    }

}
