package com.channel360.audit.service;

import com.channel360.audit.dto.AuditLogResponse;
import com.channel360.audit.entity.AuditLog;
import com.channel360.audit.repository.AuditLogRepository;
import com.channel360.user.entity.User;
import com.channel360.user.repository.UserRepository;
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
    private final UserRepository userRepository;
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

    private AuditLogResponse toDto(AuditLog log) {
        String userName = null;
        String userEmail = null;
        if (log.getUserId() != null) {
            try {
                User u = userRepository.findById(log.getUserId()).orElse(null);
                if (u != null) {
                    userName = u.getFirstName() + " " + u.getLastName();
                    userEmail = u.getEmail();
                }
            } catch (Exception ignored) {}
        }
        return AuditLogResponse.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .userName(userName)
                .userEmail(userEmail)
                .action(log.getAction())
                .moduleName(log.getModuleName())
                .entityName(log.getEntityName())
                .entityId(log.getEntityId())
                .oldData(log.getOldData())
                .newData(log.getNewData())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
