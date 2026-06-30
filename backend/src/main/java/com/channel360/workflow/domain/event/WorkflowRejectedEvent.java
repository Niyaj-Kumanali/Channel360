package com.channel360.workflow.domain.event;

import java.time.Instant;

public record WorkflowRejectedEvent(
    Long aggregateId,
    Long requestId,
    Long rejectedBy,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "WORKFLOW_REJECTED"; }
}
