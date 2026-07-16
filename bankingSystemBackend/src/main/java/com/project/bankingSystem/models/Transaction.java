package com.project.bankingSystem.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the user/account
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double amount;
    private Double balanceAfterTransaction; // Snapshot of balance at this moment

    @Enumerated(EnumType.STRING)
    private TransactionType type; // DEPOSIT, WITHDRAWAL, TRANSFER

    private LocalDateTime createdAt;

    // For transfers: tracks where money went or came from
    private Long relatedAccountId;
}