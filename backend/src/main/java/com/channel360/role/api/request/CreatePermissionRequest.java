package com.channel360.role.api.request;

import lombok.Builder;

@Builder
public record CreatePermissionRequest(
    String name,
    String description,
    String module
) {}
