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
    @GeneratedValue
    Long id;
    Long ac;
    @NotNull
    @Column(unique = true, nullable = false)
    String username;
    @Email
    String email;
    @NotNull
    String password;
    Double balance;
    @Version
    private Long version;
}
