package com.channel360.workflow.domain.event;

import java.time.Instant;
import java.util.UUID;

public record NodeCompletedEvent(
    Long aggregateId,
    Long requestId,
    UUID nodeId,
    String nodeName,
    Instant occurredAt
) implements WorkflowEvent {
    @Override public String eventType() { return "NODE_COMPLETED"; }
}
