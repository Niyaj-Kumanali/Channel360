package com.channel360.menu.api;

import lombok.Builder;

import java.util.List;

@Builder
public record MenuWithPermissions(
    Long id,
    Long parentId,
    String label,
    String path,
    String icon,
    Integer displayOrder,
    Boolean active,
    String permissionName,
    List<PermissionItem> permissions
) {}