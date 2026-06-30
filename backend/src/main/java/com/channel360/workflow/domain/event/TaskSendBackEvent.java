package com.channel360.workflow.domain.event;

import java.time.Instant;

public record TaskSendBackEvent(
    Long aggregateId,
    Long taskId,
    Long requestId,
    Long sentBackBy,
    String comments,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "TASK_SENT_BACK"; }
}
