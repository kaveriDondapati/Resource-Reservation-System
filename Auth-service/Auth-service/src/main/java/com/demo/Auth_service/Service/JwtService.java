package com.demo.Auth_service.Service;

import com.demo.Auth_service.Entity.User;
import com.demo.Auth_service.Security.JwtPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String ISSUER = "resource-reservation";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    @PostConstruct
    void checkSecret() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 characters");
        }
    }

    public String generateToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(signingKey())
                .compact();
    }

    public JwtPrincipal parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey())
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        String role = claims.get("role", String.class);
        Object rawUserId = claims.get("userId");
        if (!(rawUserId instanceof Number number)
                || username == null
                || username.isBlank()
                || (!"USER".equals(role) && !"ADMIN".equals(role))) {
            throw new io.jsonwebtoken.JwtException("Invalid token claims");
        }
        return new JwtPrincipal(number.longValue(), username, role);
    }

    public long expiresInSeconds() {
        return expirationMs / 1000;
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
