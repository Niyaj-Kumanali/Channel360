package com.channel360.workflow.application.event.listener;

import com.channel360.workflow.domain.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class WorkflowEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWorkflowStarted(WorkflowStartedEvent event) {
        log.info("Workflow started: requestId={}, versionId={}", event.requestId(), event.workflowVersionId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWorkflowCompleted(WorkflowCompletedEvent event) {
        log.info("Workflow completed: requestId={}", event.requestId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWorkflowCancelled(WorkflowCancelledEvent event) {
        log.info("Workflow cancelled: requestId={}, by={}", event.requestId(), event.cancelledBy());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWorkflowRejected(WorkflowRejectedEvent event) {
        log.info("Workflow rejected: requestId={}, by={}", event.requestId(), event.rejectedBy());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskCreated(TaskCreatedEvent event) {
        log.info("Task created: taskId={}, requestId={}, assignedTo={}",
            event.taskId(), event.requestId(), event.assignedUserId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskApproved(TaskApprovedEvent event) {
        log.info("Task approved: taskId={}, requestId={}, by={}",
            event.taskId(), event.requestId(), event.approvedBy());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskRejected(TaskRejectedEvent event) {
        log.info("Task rejected: taskId={}, requestId={}, by={}",
            event.taskId(), event.requestId(), event.rejectedBy());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskDelegated(TaskDelegatedEvent event) {
        log.info("Task delegated: taskId={}, from={}, to={}",
            event.taskId(), event.fromUserId(), event.toUserId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskEscalated(TaskEscalatedEvent event) {
        log.info("Task escalated: taskId={}, requestId={}", event.taskId(), event.requestId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskTimedOut(TaskTimedOutEvent event) {
        log.info("Task timed out: taskId={}, requestId={}", event.taskId(), event.requestId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskSendBack(TaskSendBackEvent event) {
        log.info("Task sent back: taskId={}, requestId={}, by={}",
            event.taskId(), event.requestId(), event.sentBackBy());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNodeCompleted(NodeCompletedEvent event) {
        log.info("Node completed: requestId={}, node={}", event.requestId(), event.nodeName());
    }
}
