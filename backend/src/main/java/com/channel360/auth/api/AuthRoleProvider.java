package com.channel360.auth.api;

import com.channel360.role.api.response.RoleResponse;
import com.channel360.role.application.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthRoleProvider {

    private final RoleService roleService;

    public RoleResponse findByName(String name) {
        return roleService.getRoleByName(name);
    }
}
