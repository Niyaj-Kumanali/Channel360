package com.channel360.workflow.domain.model;

import com.channel360.workflow.domain.enums.TransitionAction;
import java.util.UUID;

public record TransitionRef(
    UUID transitionId,
    String label,
    TransitionAction action,
    UUID targetNodeId
) {
}
