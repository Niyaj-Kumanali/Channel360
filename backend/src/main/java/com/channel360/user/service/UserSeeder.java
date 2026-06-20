package com.channel360.user.service;

import com.channel360.auth.dto.RegisterRequest;
import com.channel360.auth.service.AuthService;
import com.channel360.common.config.AdminProperties;
import com.channel360.common.exception.DuplicateResourceException;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.role.entity.Role;
import com.channel360.role.repository.RoleRepository;
import com.channel360.user.entity.User;
import com.channel360.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class UserSeeder implements CommandLineRunner {

    private final AuthService authService;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AdminProperties adminProperties;

    @Override
    public void run(String... args) {
        if (adminProperties.skip()) {
            log.info("Admin seeder skipped via config");
            return;
        }

        log.info("Seeding admin user: {}", adminProperties.email());

        User adminUser;
        try {
            RegisterRequest request = new RegisterRequest();
            request.setEmail(adminProperties.email());
            request.setFirstName(adminProperties.firstName());
            request.setLastName(adminProperties.lastName());
            request.setPassword(adminProperties.password());
            request.setMobileNumber(adminProperties.mobileNumber());

            adminUser = authService.register(request);
            log.info("Created admin user: {}", adminUser.getEmail());
        } catch (DuplicateResourceException e) {
            adminUser = userRepository.findByEmail(adminProperties.email())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminProperties.email()));
            log.info("Admin user already exists: {}", adminUser.getEmail());
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_ADMIN"));

        userService.assignRoles(adminUser.getId(), java.util.List.of(adminRole.getId()));
        log.info("Assigned ROLE_ADMIN to user {}", adminUser.getEmail());
    }
}
