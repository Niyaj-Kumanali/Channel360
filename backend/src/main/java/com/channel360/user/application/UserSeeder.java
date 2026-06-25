package com.channel360.user.application;

import com.channel360.auth.api.AuthFacade;
import com.channel360.auth.api.AuthUserDto;
import com.channel360.auth.api.RegisterRequest;
import com.channel360.common.config.AdminProperties;
import com.channel360.common.config.SuperAdminProperties;
import com.channel360.common.exception.DuplicateResourceException;
import com.channel360.role.api.RoleFacade;
import com.channel360.role.api.RoleResponse;
import com.channel360.user.api.UserResponse;
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

    private final AuthFacade authFacade;
    private final UserService userService;
    private final RoleFacade roleFacade;
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

        UserResponse user;
        try {
            RegisterRequest request = new RegisterRequest();
            request.setEmail(email);
            request.setFirstName(firstName);
            request.setLastName(lastName);
            request.setPassword(password);
            request.setMobileNumber(mobileNumber);

            user = authFacade.register(request);
            log.info("Created {} user: {}", label, email);
        } catch (DuplicateResourceException e) {
            AuthUserDto authUser = authFacade.findByEmail(email);
            user = userService.getUserById(authUser.id());
            log.info("{} user already exists: {}", label, email);
        }

        RoleResponse role = roleFacade.findByName(roleName);

        userService.assignRoles(user.getId(), java.util.List.of(role.getId()));
        log.info("Assigned {} to user {}", roleName, email);
    }
}
