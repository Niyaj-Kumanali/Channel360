package com.channel360.role.api.response;

import lombok.Builder;

@Builder
public record PermissionResponse(
    Long id,
    String name,
    String description,
    String module,
    Long menuId
) {}
