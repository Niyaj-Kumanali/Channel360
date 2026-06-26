package com.channel360.region.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record RegionRequest(
    Long id,
    @NotBlank(message = "Region name is required") String name,
    Long parentId,
    @NotBlank(message = "Level is required")
    @Pattern(regexp = "^(Zone|Region|State|Territory)$", message = "Level must be Zone, Region, State, or Territory")
    String level,
    @NotBlank(message = "Tree type is required")
    @Pattern(regexp = "^(B2B|B2C)$", message = "Tree type must be B2B or B2C")
    String treeType
) {}
