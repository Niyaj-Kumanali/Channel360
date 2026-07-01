package com.channel360.role.application;

import com.channel360.role.api.request.CreatePermissionRequest;
import com.channel360.role.api.response.PermissionResponse;
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
    public void setMenuId(String permissionName, Long menuItemId) {
        permissionRepository.findByName(permissionName).ifPresent(p -> {
            p.setMenuId(menuItemId);
            permissionRepository.save(p);
        });
    }
}
