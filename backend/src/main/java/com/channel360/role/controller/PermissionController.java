package com.channel360.role.controller;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.role.entity.Permission;
import com.channel360.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Permission>>> getAllPermissions() {
        return ResponseEntity.ok(ApiResponse.success(roleService.getAllPermissions()));
    }
}
