package com.channel360.role.api;

import java.util.List;
import lombok.Builder;

@Builder
public record RoleResponse(
    Long id,
    String name,
    String description,
    List<String> permissions,
    List<Long> permissionIds
) {}
