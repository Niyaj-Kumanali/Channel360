package com.channel360.role.api;

public record PermissionResponse(
    Long id,
    String name,
    String description,
    String module,
    Long menuId
) {}
