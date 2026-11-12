package com.demo.Auth_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Schema(example = "admin")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(example = "Admin@123")
    private String password;
}
