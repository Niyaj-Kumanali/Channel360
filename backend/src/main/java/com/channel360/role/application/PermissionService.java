package com.channel360.role.application;

import com.channel360.role.api.CreatePermissionRequest;
import com.channel360.role.api.PermissionResponse;
import com.channel360.role.domain.Permission;
import com.channel360.role.infrastructure.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional
    public PermissionResponse createIfNotExists(CreatePermissionRequest request) {
        Permission permission = permissionRepository.findByName(request.getName())
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setName(request.getName());
                    p.setDescription(request.getDescription());
                    p.setModule(request.getModule());
                    return permissionRepository.save(p);
                });
        return new PermissionResponse(
                permission.getId(), permission.getName(),
                permission.getDescription(), permission.getModule(), permission.getMenuId()
        );
    }

    @Transactional
    public void setMenuId(String permissionName, Long menuItemId) {
        permissionRepository.findByName(permissionName).ifPresent(p -> {
            p.setMenuId(menuItemId);
            permissionRepository.save(p);
        });
    }
}
