package com.project.bankingSystem.models;


import lombok.Getter;

@Getter
public class Payment {

    private Long from_id;
    private Long to_id;
    private Double amount;
}
