package com.channel360.common.event;

import com.channel360.audit.application.AuditService;
import com.channel360.common.service.EmailService;
import com.channel360.role.domain.event.RoleCreatedEvent;
import com.channel360.role.domain.event.RoleDeletedEvent;
import com.channel360.role.domain.event.RoleUpdatedEvent;
import com.channel360.user.domain.event.RoleAssignedEvent;
import com.channel360.user.domain.event.UserCreatedEvent;
import com.channel360.user.domain.event.UserUpdatedEvent;
import com.channel360.workflow.domain.event.WorkflowApprovedEvent;
import com.channel360.workflow.domain.event.WorkflowCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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
    public void handleUserUpdated(UserUpdatedEvent event) {
        log.info("User updated: userId={}, email={}", event.getUserId(), event.getEmail());
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

    @EventListener
    public void handleRoleDeleted(RoleDeletedEvent event) {
        log.info("Role deleted: roleId={}", event.getRoleId());
    }

    @EventListener
    public void handleWorkflowCreated(WorkflowCreatedEvent event) {
        log.info("Workflow created: workflowId={}, name={}, module={}",
                event.getWorkflowId(), event.getName(), event.getModule());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWorkflowApproved(WorkflowApprovedEvent event) {
        log.info("Workflow approved: requestId={}, stepId={}, approvedBy={}",
                event.getRequestId(), event.getStepId(), event.getApprovedBy());
    }
}
