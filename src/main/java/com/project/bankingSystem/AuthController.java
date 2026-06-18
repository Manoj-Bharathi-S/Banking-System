package com.project.bankingSystem;


import com.project.bankingSystem.models.User;
import com.project.bankingSystem.repositories.UserRepository;

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

        String hashToCheck = userOptional.isPresent()
                ? userOptional.get().getPassword()
                : "$2a$10$S8Z4UcL9NbdGTVLZVwp9RP.Kxq90K3BMU6GEgwdRuIHQBlzHzH0iFS";

        boolean passwordMatches = passwordEncoder.matches(password, hashToCheck);

        if (userOptional.isEmpty() || !passwordMatches) {
            return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(username);
        return ResponseEntity.ok(Map.of("token",token));

    }
}
