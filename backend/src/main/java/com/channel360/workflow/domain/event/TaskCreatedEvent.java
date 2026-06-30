package com.channel360.workflow.domain.event;

import java.time.Instant;

public record TaskCreatedEvent(
    Long aggregateId,
    Long taskId,
    Long requestId,
    Long assignedUserId,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "TASK_CREATED"; }
}
