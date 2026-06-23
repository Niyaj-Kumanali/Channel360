package com.channel360.auth.api;

import com.channel360.auth.application.AuthService;
import com.channel360.auth.domain.AuthUser;
import com.channel360.auth.infrastructure.AuthUserRepository;
import com.channel360.user.api.UserFacade;
import com.channel360.user.api.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final AuthService authService;
    private final AuthUserRepository authUserRepository;
    private final UserFacade userFacade;

    @Override
    public UserResponse register(RegisterRequest request) {
        return authService.register(request);
    }

    @Override
    public AuthUserDto findByEmail(String email) {
        AuthUser user = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        UserResponse profile = userFacade.getById(user.getId());
        return toAuthDto(user, profile);
    }

    @Override
    public AuthUserDto getAuthById(Long id) {
        AuthUser user = authUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        UserResponse profile = userFacade.getById(id);
        return toAuthDto(user, profile);
    }

    @Override
    public boolean existsByEmail(String email) {
        return authUserRepository.existsByEmail(email);
    }

    @Override
    public Long saveAuthUser(String email, String password, Long userId, String createdBy) {
        AuthUser user = AuthUser.builder()
                .id(userId)
                .email(email)
                .password(password)
                .build();
        authUserRepository.save(user);
        return userId;
    }

    @Override
    public void changePassword(Long userId, String encodedPassword) {
        authUserRepository.spChangePassword(userId, encodedPassword);
    }

    private AuthUserDto toAuthDto(AuthUser authUser, UserResponse profile) {
        return AuthUserDto.builder()
                .id(authUser.getId())
                .email(authUser.getEmail())
                .password(authUser.getPassword())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .status(profile.getStatus())
                .deletedFlag(authUser.isDeletedFlag())
                .lastLoginAt(authUser.getLastLoginAt())
                .roleNames(userFacade.findRoleNamesByUserId(authUser.getId()))
                .permissionNames(userFacade.findPermissionNamesByUserId(authUser.getId()))
                .build();
    }
}
