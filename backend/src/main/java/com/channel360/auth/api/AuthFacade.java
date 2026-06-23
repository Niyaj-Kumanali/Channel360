package com.channel360.auth.api;

import com.channel360.user.api.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final com.channel360.auth.application.AuthService authService;

    public UserResponse register(RegisterRequest request) {
        return authService.register(request);
    }
}
