package com.example.market.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Entity
@Table(name = "comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String content;

    private String reply;

    @ManyToOne
    private  ItemEntity salesItem;

    @ManyToOne
    private  UserEntity Users;
    public CommentEntity() {

    }
}