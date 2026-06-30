package com.channel360.workflow.application.designer.validation.rules.assignment;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CircularAssignmentRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.ASSIGNMENTS; }
    @Override
    public String ruleName() { return "CircularAssignment"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        List<ValidationError> errors = new ArrayList<>();
        for (var assignment : graph.assignments()) {
            for (var rule : assignment.rules()) {
                if (rule.userId() != null) {
                    var node = graph.nodes().stream()
                        .filter(n -> n.id().equals(assignment.id())).findFirst();
                    if (node.isPresent()) {
                        String name = node.get().name().toLowerCase();
                        if (name.contains("self") || name.contains("requestor")) {
                            errors.add(new ValidationError(ruleName(),
                                "Potential circular assignment: node '" + node.get().name()
                                + "' assigns to user " + rule.userId(),
                                "ASSIGNMENT", assignment.id().toString()));
                        }
                    }
                }
            }
        }
        return errors;
    }
}
