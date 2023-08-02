package com.example.market.authentication;

import lombok.Data;

@Data
public class JwtRequestDto {
    private String username;
    private String password;
}
