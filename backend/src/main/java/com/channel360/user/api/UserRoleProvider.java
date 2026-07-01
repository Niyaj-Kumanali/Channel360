package com.channel360.user.api;

import com.channel360.role.api.response.RoleResponse;
import com.channel360.role.application.RoleService;
import com.channel360.role.infrastructure.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleProvider {

    private final RoleService roleService;
    private final RoleRepository roleRepository;

    public RoleResponse getById(Long id) {
        return roleService.getRoleById(id);
    }

    public boolean existsById(Long id) {
        return roleRepository.existsById(id);
    }
}
