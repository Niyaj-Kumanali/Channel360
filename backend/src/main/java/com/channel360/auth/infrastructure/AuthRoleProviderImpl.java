package com.channel360.auth.infrastructure;

import com.channel360.auth.api.AuthRoleProvider;
import com.channel360.role.api.response.RoleResponse;
import com.channel360.role.application.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthRoleProviderImpl implements AuthRoleProvider {

    private final RoleService roleService;

    @Override
    public RoleResponse findByName(String name) {
        return roleService.getRoleByName(name);
    }
}
