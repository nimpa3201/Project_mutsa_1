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
    private Long itemId;
    @NonNull
    private Long suggestedPrice;
    private String status;
    @NonNull
    private String writer;

    @NonNull
    private String password;

    public NegotiationEntity(){

    }

}
