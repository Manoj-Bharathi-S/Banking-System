package com.project.bankingSystem;


import com.project.bankingSystem.models.User;
import com.project.bankingSystem.UserRepository;
import  com.project.bankingSystem.UserService;

import com.project.bankingSystem.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User body){
        String username = body.getUsername();
        body.setPassword(passwordEncoder.encode(body.getPassword()));
        body.setBalance(0.0);
        if(userRepository.findByUsername(username).isPresent()){
            return new ResponseEntity<>("User already Exists", HttpStatus.CONFLICT);
        }

        userService.createUser(body);
        return new ResponseEntity<>("Successfully Registered", HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String,String> body){
        String username = body.get("username");
        String password = body.get("password");
        var userOptional = userRepository.findByUsername(username);
        if(userOptional.isEmpty()){
            return new ResponseEntity<>("User does not Exist", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        if(!passwordEncoder.matches(password,user.getPassword())){
            return new ResponseEntity<>("Invalid Password",HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtil.generateToken(username);
        return ResponseEntity.ok(Map.of("token",token));

    }
}
