package com.channel360.menu.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MenuResponse(
    Long id,
    @JsonInclude(JsonInclude.Include.ALWAYS) Long parentId,
    String label,
    String path,
    String icon,
    Integer displayOrder,
    Boolean active,
    String permissionName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}