package com.channel360.workflow.domain.event;

import java.time.Instant;

public sealed interface WorkflowEvent
    permits TaskCreatedEvent, TaskAssignedEvent, TaskApprovedEvent, TaskRejectedEvent,
            TaskDelegatedEvent, TaskEscalatedEvent, TaskTimedOutEvent, TaskSendBackEvent,
            NodeCompletedEvent, WorkflowStartedEvent, WorkflowCompletedEvent,
            WorkflowCancelledEvent, WorkflowRejectedEvent {

    Long aggregateId();
    String eventType();
    Instant occurredAt();
}
