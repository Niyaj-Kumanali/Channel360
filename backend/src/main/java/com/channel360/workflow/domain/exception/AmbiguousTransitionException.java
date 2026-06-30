package com.channel360.workflow.domain.exception;

import java.util.UUID;

public class AmbiguousTransitionException extends RuntimeException {
    public AmbiguousTransitionException(UUID nodeId, String action, int matchingCount) {
        super("Ambiguous transition from node " + nodeId + " for action " + action
            + ": " + matchingCount + " transitions matched. Conditions are not deterministic.");
    }
}
