package com.channel360.auth.application;

import com.channel360.auth.api.request.RegisterRequest;
import com.channel360.auth.api.response.AuthUserDto;
import com.channel360.user.api.UserResponse;

public interface AuthFacade {
    UserResponse register(RegisterRequest request);
    AuthUserDto findByEmail(String email);
}
