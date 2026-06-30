package com.channel360.workflow.domain.model;

import com.channel360.workflow.domain.enums.TransitionAction;
import java.util.UUID;

public record CompiledTransition(
    UUID transitionId,
    String label,
    TransitionAction action,
    UUID sourceNodeId,
    UUID targetNodeId,
    CompiledCondition condition,
    int priority
) {
}
