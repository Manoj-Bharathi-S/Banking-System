package com.project.bankingSystem.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    Long transaction_id;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    Long user_id;
    Double credit;
    Double debit;
    Double balance;
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

}
