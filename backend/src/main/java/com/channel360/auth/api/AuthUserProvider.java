package com.channel360.auth.api;

import com.channel360.user.api.response.UserResponse;

import java.util.List;
import java.util.Set;

public interface AuthUserProvider {
    UserResponse getById(Long id);
    Set<String> findRoleNamesByUserId(Long userId);
    Set<String> findPermissionNamesByUserId(Long userId);
    void assignRoles(Long userId, List<Long> roleIds);
}
