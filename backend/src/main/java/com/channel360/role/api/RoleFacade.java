package com.channel360.role.api;

import org.springframework.lang.NonNull;

public interface RoleFacade {
    RoleResponse getById(Long id);
    RoleResponse findByName(String name);
    boolean existsById(Long id);
    String getRoleNameById(@NonNull Long id);
}
