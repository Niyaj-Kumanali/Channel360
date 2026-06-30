package com.channel360.workflow.domain.exception;

import java.util.UUID;

public class NoValidTransitionException extends RuntimeException {
    public NoValidTransitionException(UUID nodeId, String action) {
        super("No valid transition from node " + nodeId + " for action: " + action);
    }
}
