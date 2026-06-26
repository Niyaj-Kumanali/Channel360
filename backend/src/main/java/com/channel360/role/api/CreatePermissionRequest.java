package com.channel360.role.api;

public record CreatePermissionRequest(
    String name,
    String description,
    String module
) {}
