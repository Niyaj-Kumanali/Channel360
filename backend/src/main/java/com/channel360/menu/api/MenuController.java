package com.channel360.menu.api;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import com.channel360.common.service.MenuService;
import com.channel360.menu.api.MenuRequest;
import com.channel360.menu.api.MenuResponse;
import com.channel360.menu.api.MenuWithPermissions;
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
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenuItemById(id)));
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
        for (MenuRequest item : items) {
            menuService.reorderMenu(item.getId(), item.getDisplayOrder());
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Menu items reordered"));
    }

    @GetMapping("/with-permissions")
    @RequirePermission("menu.manage")
    public ResponseEntity<ApiResponse<List<MenuWithPermissions>>> getMenusWithPermissions() {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenusWithPermissions()));
    }
}
