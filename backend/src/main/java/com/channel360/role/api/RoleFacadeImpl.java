package com.channel360.role.api;

import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.menu.api.PermissionItem;
import com.channel360.role.domain.Permission;
import com.channel360.role.domain.Role;
import com.channel360.role.infrastructure.PermissionRepository;
import com.channel360.role.infrastructure.RoleRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleFacadeImpl implements RoleFacade {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public RoleResponse getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return toResponse(role);
    }

    @Override
    public RoleResponse findByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", name));
        return toResponse(role);
    }

    @Override
    public boolean existsById(Long id) {
        return roleRepository.existsById(id);
    }

    @Override
    public List<PermissionItem> findPermissionsByMenuId(Long menuId) {
        List<Permission> permissions = permissionRepository.findByMenuId(menuId);
        return permissions.stream()
                .map(p -> PermissionItem.builder().id(p.getId()).name(p.getName()).description(p.getDescription()).module(p.getModule()).build())
                .toList();
    }

    @Override
    public String getRoleNameById(@NonNull Long id) {
        return roleRepository.findById(id)
                .map(Role::getName)
                .orElse(null);
    }

    private RoleResponse toResponse(Role role) {
        List<String> permissionNames = role.getPermissions().stream()
                .map(Permission::getName)
                .toList();
        List<Long> permissionIds = role.getPermissions().stream()
                .map(Permission::getId)
                .toList();
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissionNames)
                .permissionIds(permissionIds)
                .build();
    }
}
