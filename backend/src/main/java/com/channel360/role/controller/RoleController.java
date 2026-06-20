package com.channel360.role.controller;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import com.channel360.role.dto.response.RoleResponse;
import com.channel360.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        return ResponseEntity.ok(ApiResponse.success(roleService.getAllRoles()));
    }

    @PostMapping
    @RequirePermission("roles.manage")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleResponse roleDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(roleService.createRole(roleDto), "Role created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getRoleById(id)));
    }

    @PutMapping("/{id}")
    @RequirePermission("roles.manage")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable Long id, @Valid @RequestBody RoleResponse roleDto) {
        return ResponseEntity.ok(ApiResponse.success(roleService.updateRole(id, roleDto), "Role updated successfully"));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("roles.manage")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Role deleted successfully"));
    }
}
