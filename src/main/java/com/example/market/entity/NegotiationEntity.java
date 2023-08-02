package com.example.market.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@Table(name = "negotiation")
public class NegotiationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private Long suggestedPrice;
    private String status;


    @ManyToOne
    private  UserEntity Users;

    @ManyToOne
    private ItemEntity salesItem;


    public NegotiationEntity(){

    }

}
