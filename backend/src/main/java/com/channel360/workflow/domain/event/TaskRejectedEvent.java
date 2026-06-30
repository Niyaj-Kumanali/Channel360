package com.channel360.workflow.domain.event;

import java.time.Instant;

public record TaskRejectedEvent(
    Long aggregateId,
    Long taskId,
    Long requestId,
    Long rejectedBy,
    String comments,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "TASK_REJECTED"; }
}
