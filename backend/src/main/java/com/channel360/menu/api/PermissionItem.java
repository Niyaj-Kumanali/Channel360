package com.channel360.menu.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionItem {
    private Long id;
    private String name;
    private String description;
    private String module;
}
