package com.demo.Reservation_service.Security;

import org.springframework.security.core.AuthenticatedPrincipal;

public record JwtPrincipal(Long userId, String username, String role) implements AuthenticatedPrincipal {

    @Override
    public String getName() {
        return username;
    }
}
