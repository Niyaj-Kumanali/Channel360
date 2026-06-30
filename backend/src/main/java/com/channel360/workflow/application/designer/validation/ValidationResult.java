package com.channel360.workflow.application.designer.validation;

import java.util.Collections;
import java.util.List;

public record ValidationResult(boolean valid, List<ValidationError> errors) {
    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyList());
    }

    public static ValidationResult failure(List<ValidationError> errors) {
        return new ValidationResult(false, errors);
    }
}
