package com.example.springboottemplate.dto;

import lombok.Data;

@Data
public class RefreshTokenRequestDto {
    private String refreshToken;
    private String username;
}
