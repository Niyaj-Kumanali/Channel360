package com.channel360.region.api;

import lombok.Builder;

@Builder
public record RegionResponse(
    Long id,
    String name,
    Long parentId,
    String level,
    String treeType,
    String path,
    String createdBy,
    String updatedBy
) {}