package com.channel360.region.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionResponse {

    private Long id;
    private String name;
    private Long parentId;
    private String level;
    private String treeType;
    private String path;
    private String createdBy;
    private String updatedBy;
}
