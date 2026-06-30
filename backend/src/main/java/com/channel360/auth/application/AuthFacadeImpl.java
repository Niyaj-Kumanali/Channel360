package com.channel360.auth.application;

import com.channel360.auth.api.request.RegisterRequest;
import com.channel360.auth.api.response.AuthUserDto;
import com.channel360.auth.domain.AuthUser;
import com.channel360.auth.infrastructure.AuthUserRepository;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.user.api.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final AuthService authService;
    private final AuthUserRepository authUserRepository;

    @Override
    public UserResponse register(RegisterRequest request) {
        return authService.register(request);
    }

    @Override
    public AuthUserDto findByEmail(String email) {
        AuthUser user = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return buildAuthUserDto(user);
    }

    @Override
    public AuthUserDto getAuthById(Long id) {
        AuthUser user = authUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return buildAuthUserDto(user);
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

    private AuthUserDto buildAuthUserDto(AuthUser authUser) {
        return AuthUserDto.builder()
                .id(authUser.getId())
                .email(authUser.getEmail())
                .password(authUser.getPassword())
                .deletedFlag(authUser.isDeletedFlag())
                .lastLoginAt(authUser.getLastLoginAt())
                .build();
    }
}
