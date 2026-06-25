package com.channel360.menu.api;

import lombok.Builder;

@Builder
public record PermissionItem(
    Long id,
    String name,
    String description,
    String module
) {}