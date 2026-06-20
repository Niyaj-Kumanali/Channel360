package com.channel360.menu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuRequest {
    private Long id;

    private Long parentId;

    @NotBlank(message = "Label is required")
    private String label;

    @NotBlank(message = "Path is required")
    private String path;

    private String icon;

    private Integer displayOrder;

    private Boolean active;

    private String permissionName;
}
