package com.channel360.user.application;

import com.channel360.auth.api.RegisterRequest;
import com.channel360.auth.application.AuthService;
import com.channel360.common.config.AdminProperties;
import com.channel360.common.config.SuperAdminProperties;
import com.channel360.common.exception.DuplicateResourceException;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.role.domain.Role;
import com.channel360.role.infrastructure.RoleRepository;
import com.channel360.user.domain.User;
import com.channel360.user.infrastructure.UserRepository;
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
    private final SuperAdminProperties superAdminProperties;

    @Override
    public void run(String... args) {
        seedAdmin(
                adminProperties.email(), adminProperties.password(),
                adminProperties.firstName(), adminProperties.lastName(),
                adminProperties.mobileNumber(), adminProperties.skip(),
                "ROLE_ADMIN", "Admin"
        );
        seedAdmin(
                superAdminProperties.email(), superAdminProperties.password(),
                superAdminProperties.firstName(), superAdminProperties.lastName(),
                superAdminProperties.mobileNumber(), superAdminProperties.skip(),
                "ROLE_SUPER_ADMIN", "Super Admin"
        );
    }

    private void seedAdmin(
            String email, String password, String firstName, String lastName,
            String mobileNumber, boolean skip,
            String roleName, String label
    ) {
        if (skip) {
            log.info("{} seeder skipped via config", label);
            return;
        }

        log.info("Seeding {} user: {}", label, email);

        User user;
        try {
            RegisterRequest request = new RegisterRequest();
            request.setEmail(email);
            request.setFirstName(firstName);
            request.setLastName(lastName);
            request.setPassword(password);
            request.setMobileNumber(mobileNumber);

            user = authService.register(request);
            log.info("Created {} user: {}", label, user.getEmail());
        } catch (DuplicateResourceException e) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
            log.info("{} user already exists: {}", label, user.getEmail());
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));

        userService.assignRoles(user.getId(), java.util.List.of(role.getId()));
        log.info("Assigned {} to user {}", roleName, user.getEmail());
    }
}
