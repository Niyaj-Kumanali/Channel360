package com.channel360.workflow.domain.event;

import java.time.Instant;

public record WorkflowStartedEvent(
    Long aggregateId,
    Long requestId,
    Long workflowVersionId,
    Long requestorId,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "WORKFLOW_STARTED"; }
}
