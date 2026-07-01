package com.channel360.auth.application;

import com.channel360.auth.api.AuthUserProvider;
import com.channel360.auth.api.request.RegisterRequest;
import com.channel360.auth.api.response.AuthUserDto;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.user.api.response.UserResponse;
import com.channel360.user.domain.User;
import com.channel360.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final AuthUserProvider authUserProvider;

    @Override
    public UserResponse register(RegisterRequest request) {
        return authService.register(request);
    }

    @Override
    public AuthUserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return buildAuthUserDto(user);
    }

    private AuthUserDto buildAuthUserDto(User user) {
        return AuthUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus())
                .deletedFlag(user.isDeletedFlag())
                .lastLoginAt(user.getLastLoginAt())
                .roleNames(authUserProvider.findRoleNamesByUserId(user.getId()))
                .permissionNames(authUserProvider.findPermissionNamesByUserId(user.getId()))
                .build();
    }
}
