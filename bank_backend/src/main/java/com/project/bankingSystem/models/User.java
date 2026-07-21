package com.project.bankingSystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "users", indexes = {
        @Index(name = "idx_username", columnList = "username", unique = true)
})
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ac;
    @NotNull
    @Column(unique = true, nullable = false)
    private String username;
    @Email
    private String email;
    @NotNull
    private String password;
    private Double balance;
    @Version
    private Long version;
}
