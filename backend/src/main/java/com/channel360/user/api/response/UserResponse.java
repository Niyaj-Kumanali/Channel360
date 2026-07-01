package com.channel360.user.api.response;

import com.channel360.role.api.response.RoleResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder(toBuilder = true)
public record UserResponse(
    Long id,
    String employeeId,
    String firstName,
    String lastName,
    String email,
    String mobileNumber,
    String status,
    LocalDateTime lastLoginAt,
    Set<RoleResponse> roles,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
