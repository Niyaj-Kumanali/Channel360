package com.channel360.role.service;

import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.role.dto.response.RoleResponse;
import com.channel360.role.entity.Permission;
import com.channel360.role.entity.Role;
import com.channel360.role.mapper.RoleMapper;
import com.channel360.role.repository.PermissionRepository;
import com.channel360.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toDto)
                .toList();
    }

    public RoleResponse getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Transactional
    public RoleResponse createRole(RoleResponse roleDto) {
        roleRepository.spSave(null, roleDto.getName(), roleDto.getDescription());
        Role saved = roleRepository.findByName(roleDto.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleDto.getName()));

        if (roleDto.getPermissionIds() != null && !roleDto.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = roleDto.getPermissionIds().stream()
                    .map(id -> permissionRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id)))
                    .collect(Collectors.toSet());
            saved.setPermissions(permissions);
            roleRepository.save(saved);
        }

        return roleMapper.toDto(saved);
    }

    @Transactional
    public RoleResponse updateRole(Long id, RoleResponse roleDto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        roleRepository.spSave(id, roleDto.getName(), roleDto.getDescription());

        if (roleDto.getPermissionIds() != null) {
            Set<Permission> permissions = roleDto.getPermissionIds().stream()
                    .map(pid -> permissionRepository.findById(pid)
                            .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", pid)))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
            roleRepository.save(role);
        }

        return roleMapper.toDto(roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id)));
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }
        roleRepository.spDelete(id);
    }
}
