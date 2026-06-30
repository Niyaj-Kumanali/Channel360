package com.channel360.workflow.domain.event;

import java.time.Instant;

public record TaskDelegatedEvent(
    Long aggregateId,
    Long taskId,
    Long requestId,
    Long fromUserId,
    Long toUserId,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "TASK_DELEGATED"; }
}
