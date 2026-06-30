package com.channel360.menu.api;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import com.channel360.menu.application.MenuApplicationService;
import com.channel360.menu.api.MenuRequest;
import com.channel360.menu.api.MenuResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-items")
@RequiredArgsConstructor
public class MenuController {

    private final MenuApplicationService menuService;

    @GetMapping
    @RequirePermission("menu.manage")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAllMenuItems() {
        return ResponseEntity.ok(ApiResponse.success(menuService.getAllMenuItems()));
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

}
