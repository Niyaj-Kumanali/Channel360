package com.channel360.auth.api;

import com.channel360.user.api.response.UserResponse;
import com.channel360.user.application.UserService;
import com.channel360.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthUserProvider {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserResponse getById(Long id) {
        return userService.getUserById(id);
    }

    public Set<String> findRoleNamesByUserId(Long userId) {
        return userRepository.findRoleNamesByUserId(userId);
    }

    public Set<String> findPermissionNamesByUserId(Long userId) {
        return userRepository.findPermissionNamesByUserId(userId);
    }

    public void assignRoles(Long userId, List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
    }
}
