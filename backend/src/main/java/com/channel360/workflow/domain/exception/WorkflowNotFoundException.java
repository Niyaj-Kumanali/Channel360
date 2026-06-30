package com.channel360.workflow.domain.exception;

public class WorkflowNotFoundException extends RuntimeException {
    public WorkflowNotFoundException(String message) {
        super(message);
    }

    public WorkflowNotFoundException(Long id) {
        super("Workflow not found: " + id);
    }
}
