package com.demo.Auth_service.Service;

import com.demo.Auth_service.Dto.LoginRequest;
import com.demo.Auth_service.Dto.LoginResponse;
import com.demo.Auth_service.Dto.RegisterRequest;
import com.demo.Auth_service.Dto.RegisterResponse;
import com.demo.Auth_service.Entity.Role;
import com.demo.Auth_service.Entity.User;
import com.demo.Auth_service.Repository.UserRepo;
import com.demo.Auth_service.Security.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        if (userRepo.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        User saved = userRepo.save(user);

        return new RegisterResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getRole().name(),
                "Registered successfully"
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepo.findByUsername(request.getUsername().trim()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return new LoginResponse(
                jwtService.generateToken(user),
                "Bearer",
                jwtService.expiresInSeconds(),
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    public Map<String, Object> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtPrincipal principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("userId", principal.userId());
        body.put("username", principal.username());
        body.put("role", principal.role());
        return body;
    }
}
