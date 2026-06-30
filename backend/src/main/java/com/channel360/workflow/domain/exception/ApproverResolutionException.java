package com.channel360.workflow.domain.exception;

import java.util.UUID;

public class ApproverResolutionException extends RuntimeException {
    public ApproverResolutionException(UUID nodeId, String reason) {
        super("Failed to resolve approvers for node " + nodeId + ": " + reason);
    }
}
