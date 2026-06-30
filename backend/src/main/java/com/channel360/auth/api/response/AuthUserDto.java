package com.channel360.auth.api.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record AuthUserDto(
    Long id,
    String email,
    String password,
    String firstName,
    String lastName,
    String status,
    boolean deletedFlag,
    LocalDateTime lastLoginAt,
    Set<String> roleNames,
    Set<String> permissionNames
) {}