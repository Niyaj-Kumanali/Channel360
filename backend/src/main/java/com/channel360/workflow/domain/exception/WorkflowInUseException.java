package com.channel360.workflow.domain.exception;

public class WorkflowInUseException extends RuntimeException {
    public WorkflowInUseException(Long workflowId) {
        super("Workflow " + workflowId + " has active requests and cannot be modified");
    }
}
