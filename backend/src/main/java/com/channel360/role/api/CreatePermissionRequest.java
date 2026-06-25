package com.channel360.role.api;

import lombok.Data;

@Data
public class CreatePermissionRequest {
    private String name;
    private String description;
    private String module;
}
