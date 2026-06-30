package com.channel360.workflow.domain.event;

import java.time.Instant;

public record TaskEscalatedEvent(
    Long aggregateId,
    Long taskId,
    Long requestId,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "TASK_ESCALATED"; }
}
