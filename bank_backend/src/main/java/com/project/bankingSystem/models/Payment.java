package com.project.bankingSystem.models;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {

    private Long from_id;
    private Long to_id;
    private Double amount;
}
