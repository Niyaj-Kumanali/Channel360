package com.channel360.menu.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {
    private Long id;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Long parentId;
    private String label;
    private String path;
    private String icon;
    private Integer displayOrder;
    private Boolean active;
    private String permissionName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
