package com.channel360.workflow.domain.event;

import java.time.Instant;

public record WorkflowCompletedEvent(
    Long aggregateId,
    Long requestId,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "WORKFLOW_COMPLETED"; }
}
