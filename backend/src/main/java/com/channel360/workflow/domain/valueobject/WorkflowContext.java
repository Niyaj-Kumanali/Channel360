package com.channel360.workflow.domain.valueobject;

import java.time.Instant;
import java.util.UUID;

public record WorkflowContext(
    Long requestId,
    Long workflowVersionId,
    UUID currentNodeId,
    String actionUserId,
    BusinessContext businessContext,
    Instant startedAt
) {
}
