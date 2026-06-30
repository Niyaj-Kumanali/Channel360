package com.channel360.workflow.domain.event;

import java.time.Instant;

public record TaskTimedOutEvent(
    Long aggregateId,
    Long taskId,
    Long requestId,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "TASK_TIMED_OUT"; }
}
