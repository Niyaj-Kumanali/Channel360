package com.channel360.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String action;
    private String moduleName;
    private String entityName;
    private Long entityId;
    private String oldData;
    private String newData;
    private LocalDateTime createdAt;
}
