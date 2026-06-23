package com.channel360.role.api;

import com.channel360.menu.api.PermissionItem;
import org.springframework.lang.NonNull;

import java.util.List;

public interface RoleFacade {
    RoleResponse getById(Long id);
    RoleResponse findByName(String name);
    boolean existsById(Long id);
    List<PermissionItem> findPermissionsByMenuId(Long menuId);
    String getRoleNameById(@NonNull Long id);
}
