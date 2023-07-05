package com.example.market.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@Table(name = "sales_item")

public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NonNull
    private String title;
    @NonNull
    private String description;

    private String image_url;
    @NonNull
    private Integer min_price_wanted;

    private String status;
    @NonNull
    private String writer;
    @NonNull
    private String  password;

    public ItemEntity() {

    }
}
