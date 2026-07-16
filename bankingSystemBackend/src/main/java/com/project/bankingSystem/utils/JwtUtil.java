package com.project.bankingSystem.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET = "JWT Secret key string must be at least 32 bytes (256 bits) long! your-super-secret-key-here";
    private final long EXPIRATION = 1000 * 60 * 60;
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    public String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();           // 5. Get the claims data
    }
    public boolean validateJwtToken(String token){
        try{
            extractUsername(token);

            return true;
        }
        catch (JwtException exception){
            return false;

        }
    }


}
