package com.channel360.menu.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuWithPermissions {
    private Long id;
    private Long parentId;
    private String label;
    private String path;
    private String icon;
    private Integer displayOrder;
    private Boolean active;
    private String permissionName;
    private List<PermissionItem> permissions;
}
