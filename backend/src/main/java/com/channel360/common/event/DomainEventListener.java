package com.channel360.common.event;

import com.channel360.audit.application.AuditService;
import com.channel360.common.service.EmailService;
import com.channel360.role.domain.event.RoleCreatedEvent;
import com.channel360.role.domain.event.RoleUpdatedEvent;
import com.channel360.user.domain.event.RoleAssignedEvent;
import com.channel360.user.domain.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventListener {

    private final AuditService auditService;
    private final EmailService emailService;

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("User created: userId={}, email={}", event.getUserId(), event.getEmail());
    }

    @EventListener
    public void handleRoleAssigned(RoleAssignedEvent event) {
        log.info("Roles assigned to user {}: roleIds={}", event.getUserId(), event.getRoleIds());
    }

    @EventListener
    public void handleRoleCreated(RoleCreatedEvent event) {
        log.info("Role created: roleId={}, name={}", event.getRoleId(), event.getRoleName());
    }

    @EventListener
    public void handleRoleUpdated(RoleUpdatedEvent event) {
        log.info("Role updated: roleId={}, name={}", event.getRoleId(), event.getRoleName());
    }
}
