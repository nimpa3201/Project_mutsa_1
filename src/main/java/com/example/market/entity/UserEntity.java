package com.example.market.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

// Spring에 저장하고 싶은 사용자 정보

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String password;
    private String address;
    private String email;
    private String phone;


    @OneToMany(mappedBy = "Users")
    private List<ItemEntity> Items;
    @OneToMany(mappedBy = "Users")
    private List<CommentEntity> comments;
    @OneToMany(mappedBy = "Users")
    private List<NegotiationEntity> negos;

}