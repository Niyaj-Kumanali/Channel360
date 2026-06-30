package com.channel360.workflow.domain.model;

import com.channel360.workflow.domain.enums.TransitionAction;
import java.util.UUID;

public record AuditPlan(
    UUID fromNodeId,
    UUID toNodeId,
    TransitionAction action,
    String actorId,
    String comment
) {
}
