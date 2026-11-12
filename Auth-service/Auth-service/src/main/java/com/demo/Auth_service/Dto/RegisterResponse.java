package com.demo.Auth_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RegisterResponse {
    private Long  id;
    private String username;
    private String role;
    private String message;
}
