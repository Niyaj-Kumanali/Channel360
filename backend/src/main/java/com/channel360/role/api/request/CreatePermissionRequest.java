package com.channel360.role.api.request;

public record CreatePermissionRequest(
    String name,
    String description,
    String module
) {}
