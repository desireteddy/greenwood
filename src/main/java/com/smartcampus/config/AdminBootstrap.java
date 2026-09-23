package com.smartcampus.config;

import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SystemSettingsService settingsService;

    @Value("${greenwood.admin.username:admin}")
    private String adminUsername;

    @Value("${greenwood.admin.password:Greenwood@12345}")
    private String adminPassword;

    public AdminBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          SystemSettingsService settingsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.settingsService = settingsService;
    }

    @Override
    public void run(String... args) {
        settingsService.seedDefaults();
        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(u -> "ADMIN".equalsIgnoreCase(u.getRole()));
        if (!adminExists) {
            String username = adminUsername.trim();
            User existing = userRepository.findByUsername(username);
            if (existing != null) {
                throw new IllegalStateException("The configured first-run administrator username '" + username
                        + "' is already used by a non-admin account. Set GREENWOOD_ADMIN_USERNAME to an unused username.");
            }
            if (adminPassword == null || adminPassword.length() < 8) {
                throw new IllegalStateException("GREENWOOD_ADMIN_PASSWORD must contain at least 8 characters.");
            }
            User admin = new User();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");
            admin.setMustChangePassword(true);
            userRepository.save(admin);
        }
    }
}
