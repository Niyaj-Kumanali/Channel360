package com.channel360.workflow.domain.event;

import java.time.Instant;

public record TaskApprovedEvent(
    Long aggregateId,
    Long taskId,
    Long requestId,
    Long approvedBy,
    String comments,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "TASK_APPROVED"; }
}
