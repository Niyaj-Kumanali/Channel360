package com.channel360.menu.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MenuRequest(
    Long id,
    Long parentId,
    @NotBlank(message = "Label is required") String label,
    @NotBlank(message = "Path is required") String path,
    String icon,
    Integer displayOrder,
    Boolean active,
    String permissionName
) {}
