package com.channel360.menu.controller;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import com.channel360.common.service.MenuService;
import com.channel360.menu.dto.MenuRequest;
import com.channel360.menu.dto.MenuResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-items")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    @RequirePermission("menu.manage")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAllMenuItems() {
        return ResponseEntity.ok(ApiResponse.success(menuService.getAllMenuItems()));
    }

    @GetMapping("/{id}")
    @RequirePermission("menu.manage")
    public ResponseEntity<ApiResponse<MenuResponse>> getMenuItem(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenuItem(id)));
    }

    @PostMapping
    @RequirePermission("menu.manage")
    public ResponseEntity<ApiResponse<MenuResponse>> createMenuItem(@Valid @RequestBody MenuRequest request) {
        return ResponseEntity.ok(ApiResponse.success(menuService.createMenuItem(request), "Menu item created"));
    }

    @PutMapping("/{id}")
    @RequirePermission("menu.manage")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuRequest request) {
        return ResponseEntity.ok(ApiResponse.success(menuService.updateMenuItem(id, request), "Menu item updated"));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("menu.manage")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Menu item deleted"));
    }

    @PutMapping("/reorder")
    @RequirePermission("menu.manage")
    public ResponseEntity<ApiResponse<Void>> reorderMenuItems(@RequestBody List<MenuRequest> items) {
        menuService.reorderMenuItems(items);
        return ResponseEntity.ok(ApiResponse.success(null, "Menu items reordered"));
    }
}
