package com.demo.Auth_service.Config;

import com.demo.Auth_service.Entity.Role;
import com.demo.Auth_service.Entity.User;
import com.demo.Auth_service.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String username;

    @Value("${app.admin.password}")
    private String password;

    @Override
    public void run(String... args) {
        if (userRepo.existsByUsername(username)) {
            return;
        }
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        userRepo.save(admin);
        log.info("Created default admin user '{}'", username);
    }
}
