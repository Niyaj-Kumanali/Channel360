package com.channel360.region.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionRequest {

    private Long id;

    @NotBlank(message = "Region name is required")
    private String name;

    private Long parentId;

    @NotBlank(message = "Level is required")
    @Pattern(regexp = "^(Zone|Region|State|Territory)$", message = "Level must be Zone, Region, State, or Territory")
    private String level;

    @NotBlank(message = "Tree type is required")
    @Pattern(regexp = "^(B2B|B2C)$", message = "Tree type must be B2B or B2C")
    private String treeType;
}
