package com.channel360.audit.application;

import com.channel360.audit.api.AuditLogResponse;
import com.channel360.audit.domain.AuditLog;
import com.channel360.audit.infrastructure.AuditLogRepository;
import com.channel360.auth.api.AuthFacade;
import com.channel360.user.api.UserFacade;
import com.channel360.user.api.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserFacade userFacade;
    private final AuthFacade authFacade;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long log(Long userId, String action, String moduleName, String entityName, Long entityId,
                     Object oldData, Object newData) {
        try {
            String oldJson = oldData != null ? objectMapper.writeValueAsString(oldData) : null;
            String newJson = newData != null ? objectMapper.writeValueAsString(newData) : null;
            return auditLogRepository.spInsert(userId, action, moduleName, entityName, entityId, oldJson, newJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit data for {}/{}: {}", moduleName, entityName, e.getMessage());
            return null;
        }
    }

    public Long logCreate(Long userId, String moduleName, String entityName, Long entityId, Object newData) {
        return log(userId, "CREATE", moduleName, entityName, entityId, null, newData);
    }

    public Long logUpdate(Long userId, String moduleName, String entityName, Long entityId, Object oldData, Object newData) {
        return log(userId, "UPDATE", moduleName, entityName, entityId, oldData, newData);
    }

    public Long logDelete(Long userId, String moduleName, String entityName, Long entityId, Object oldData) {
        return log(userId, "DELETE", moduleName, entityName, entityId, oldData, null);
    }

    public List<AuditLogResponse> getAllLogs(String module, String action, Long userId) {
        List<AuditLog> logs;
        if (userId != null) {
            logs = auditLogRepository.findByUserId(userId);
        } else if (module != null && !module.isEmpty()) {
            logs = auditLogRepository.findByModule(module);
        } else {
            logs = auditLogRepository.findAllOrderByCreatedAtDesc();
        }

        return logs.stream().map(this::toDto).toList();
    }

    private AuditLogResponse toDto(AuditLog auditLog) {
        String userName = null;
        String userEmail = null;
        if (auditLog.getUserId() != null) {
            try {
                UserResponse u = userFacade.getById(auditLog.getUserId());
                userName = u.getFirstName() + " " + u.getLastName();
                userEmail = authFacade.getAuthById(auditLog.getUserId()).getEmail();
            } catch (Exception e) {
                log.warn("Failed to resolve user for audit log {}: {}", auditLog.getId(), e.getMessage());
            }
        }
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .userName(userName)
                .userEmail(userEmail)
                .action(auditLog.getAction())
                .moduleName(auditLog.getModuleName())
                .entityName(auditLog.getEntityName())
                .entityId(auditLog.getEntityId())
                .oldData(auditLog.getOldData())
                .newData(auditLog.getNewData())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}
