package com.channel360.common.seeder;

import com.channel360.auth.api.request.RegisterRequest;
import com.channel360.auth.application.AuthService;
import com.channel360.common.config.AdminProperties;
import com.channel360.common.config.SuperAdminProperties;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.role.api.response.RoleResponse;
import com.channel360.role.application.RoleService;
import com.channel360.user.api.response.UserResponse;
import com.channel360.user.application.UserService;
import com.channel360.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSeeder {

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final AdminProperties adminProperties;
    private final SuperAdminProperties superAdminProperties;

    public void seed() {
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

        UserResponse user;
        try {
            RegisterRequest request = RegisterRequest.builder()
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .password(password)
                    .mobileNumber(mobileNumber)
                    .build();

            user = authService.register(request);
            log.info("Created {} user: {}", label, email);
        } catch (Exception e) {
            user = userService.getUserById(
                    userRepository.findByEmail(email)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email))
                            .getId());
            log.info("{} user already exists: {}", label, email);
        }

        RoleResponse role = roleService.getRoleByName(roleName);
        userService.assignRoles(user.id(), List.of(role.id()));
        log.info("Assigned {} to user {}", roleName, email);
    }
}
