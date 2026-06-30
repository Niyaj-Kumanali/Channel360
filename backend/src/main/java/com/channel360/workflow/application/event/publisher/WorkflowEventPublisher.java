package com.channel360.workflow.application.event.publisher;

import com.channel360.workflow.application.outbox.OutboxService;
import com.channel360.workflow.domain.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class WorkflowEventPublisher {

    private final OutboxService outboxService;

    public void publish(WorkflowEvent event) {
        outboxService.saveEvent(
            event.getClass().getSimpleName(),
            event.aggregateId(),
            event.eventType(),
            event
        );
    }

    public void taskCreated(Long requestId, Long taskId, Long assignedUserId) {
        publish(new TaskCreatedEvent(requestId, taskId, requestId, assignedUserId, Instant.now()));
    }

    public void taskApproved(Long requestId, Long taskId, Long approvedBy, String comments) {
        publish(new TaskApprovedEvent(requestId, taskId, requestId, approvedBy, comments, Instant.now()));
    }

    public void taskRejected(Long requestId, Long taskId, Long rejectedBy, String comments) {
        publish(new TaskRejectedEvent(requestId, taskId, requestId, rejectedBy, comments, Instant.now()));
    }

    public void workflowCompleted(Long requestId) {
        publish(new WorkflowCompletedEvent(requestId, requestId, Instant.now()));
    }

    public void workflowStarted(Long requestId, Long workflowVersionId, Long requestorId) {
        publish(new WorkflowStartedEvent(requestId, requestId, workflowVersionId, requestorId, Instant.now()));
    }

    public void workflowCancelled(Long requestId, Long cancelledBy) {
        publish(new WorkflowCancelledEvent(requestId, requestId, cancelledBy, Instant.now()));
    }

    public void workflowRejected(Long requestId, Long rejectedBy) {
        publish(new WorkflowRejectedEvent(requestId, requestId, rejectedBy, Instant.now()));
    }
}
