package com.channel360.audit.service;

import com.channel360.audit.repository.AuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
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
}
