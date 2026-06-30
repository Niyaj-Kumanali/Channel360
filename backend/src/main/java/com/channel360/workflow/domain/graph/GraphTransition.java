package com.channel360.workflow.domain.graph;

import com.channel360.workflow.domain.enums.TransitionAction;
import java.util.UUID;

public record GraphTransition(
    UUID id,
    UUID sourceNodeId,
    UUID targetNodeId,
    TransitionAction action,
    String label,
    int priority,
    GraphConditionExpression condition
) {
}
