package com.channel360.auth.api;

import com.channel360.user.api.UserResponse;

public interface AuthFacade {
    UserResponse register(RegisterRequest request);
    AuthUserDto findByEmail(String email);
    AuthUserDto getAuthById(Long id);
    boolean existsByEmail(String email);
    Long saveAuthUser(String email, String password, Long userId, String createdBy);
    void changePassword(Long userId, String encodedPassword);
}
