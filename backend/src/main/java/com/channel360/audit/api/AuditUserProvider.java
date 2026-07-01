package com.channel360.audit.api;

import com.channel360.user.api.response.UserResponse;
import com.channel360.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditUserProvider {

    private final UserService userService;

    public UserResponse getById(Long id) {
        return userService.getUserById(id);
    }
}
