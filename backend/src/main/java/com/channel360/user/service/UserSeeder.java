package com.channel360.user.service;

import com.channel360.auth.dto.RegisterRequest;
import com.channel360.auth.service.AuthService;
import com.channel360.common.exception.DuplicateResourceException;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.role.entity.Role;
import com.channel360.role.repository.RoleRepository;
import com.channel360.user.dto.UserDto;
import com.channel360.user.entity.User;
import com.channel360.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//@Component
//@Order(1)
@Slf4j
@RequiredArgsConstructor
public class UserSeeder implements CommandLineRunner {

    private final AuthService authService;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final UserRepository userRepository;


    @Override
    public void run(String... args) throws Exception {
        log.info("UserSeeder started");

        RegisterRequest request = new RegisterRequest();
        request.setEmail("iamniyazahmad777@gmail.com");
        request.setFirstName("Niyaz");
        request.setLastName("Kumanali");
        request.setPassword("1234567890");
        request.setMobileNumber("8217097121");

        User adminUser = new User();
        try {
            adminUser = authService.register(request);
            log.info("Admin User created: {}", adminUser.getEmail());
        }catch (DuplicateResourceException e){
            adminUser = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
            log.info("User {} already exists", adminUser.getEmail());
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ADMIN"));

        log.info("Admin Role found: {}", adminRole.getName());

        UserDto userDto = userService.assignRoles(adminUser.getId(), java.util.List.of(adminRole.getId()));

        log.info("User {} assigned the role {}", userDto.getEmail(), userDto.getRoles());

    }
}
