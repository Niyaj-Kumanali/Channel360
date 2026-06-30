package com.channel360.workflow.domain.event;

import java.time.Instant;

public record WorkflowCancelledEvent(
    Long aggregateId,
    Long requestId,
    Long cancelledBy,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "WORKFLOW_CANCELLED"; }
}
