package com.channel360.auth.api;

import com.channel360.role.api.response.RoleResponse;

public interface AuthRoleProvider {
    RoleResponse findByName(String name);
}
