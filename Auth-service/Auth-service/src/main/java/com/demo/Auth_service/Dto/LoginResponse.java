package com.demo.Auth_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType;
    private long expiresIn;
    private Long userId;
    private String username;
    private String role;
}
