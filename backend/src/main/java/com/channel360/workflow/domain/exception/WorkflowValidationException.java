package com.channel360.workflow.domain.exception;

import java.util.List;

public class WorkflowValidationException extends RuntimeException {
    private final List<String> errors;

    public WorkflowValidationException(List<String> errors) {
        super("Workflow validation failed with " + errors.size() + " error(s)");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
