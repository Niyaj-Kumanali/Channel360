package com.channel360.audit.api;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuditLogResponse(
    Long id,
    Long userId,
    String userName,
    String userEmail,
    String action,
    String moduleName,
    String entityName,
    Long entityId,
    String oldData,
    String newData,
    LocalDateTime createdAt
) {}