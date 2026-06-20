package com.channel360.user.controller;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.dto.response.PageResponse;
import com.channel360.user.dto.request.CreateUserRequest;
import com.channel360.user.dto.request.UpdateUserRequest;
import com.channel360.user.dto.request.UserFilterRequest;
import com.channel360.user.dto.response.UserResponse;
import com.channel360.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(@Valid UserFilterRequest filter) {
        return ResponseEntity.ok(userService.getAllUsers(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userService.createUser(request), "User created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, request), "User updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.activateUser(id), "User activated successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.deactivateUser(id), "User deactivated successfully"));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        List<Long> roleIds = body.get("roleIds");
        return ResponseEntity.ok(ApiResponse.success(userService.assignRoles(id, roleIds), "Roles assigned successfully"));
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }
}
