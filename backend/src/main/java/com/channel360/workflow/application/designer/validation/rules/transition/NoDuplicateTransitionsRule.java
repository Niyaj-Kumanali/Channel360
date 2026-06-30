package com.channel360.workflow.application.designer.validation.rules.transition;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class NoDuplicateTransitionsRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.TRANSITIONS; }
    @Override
    public String ruleName() { return "NoDuplicateTransitions"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Set<String> seen = new HashSet<>();
        List<ValidationError> errors = new ArrayList<>();
        for (var t : graph.transitions()) {
            String key = t.sourceNodeId() + "->" + t.targetNodeId() + ":" + t.action();
            if (!seen.add(key)) {
                errors.add(new ValidationError(ruleName(), "Duplicate transition from "
                    + t.sourceNodeId() + " to " + t.targetNodeId() + " with action " + t.action(),
                    "TRANSITION", t.id().toString()));
            }
        }
        return errors;
    }
}
