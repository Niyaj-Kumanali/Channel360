package com.channel360.workflow.application.designer.validation;

public record ValidationError(String ruleName, String message, String elementType, String elementId) {
    public ValidationError(String ruleName, String message) {
        this(ruleName, message, null, null);
    }
}
