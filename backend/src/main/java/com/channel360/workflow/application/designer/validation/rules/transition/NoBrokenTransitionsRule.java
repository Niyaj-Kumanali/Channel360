package com.channel360.workflow.application.designer.validation.rules.transition;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class NoBrokenTransitionsRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.REFERENCES; }
    @Override
    public String ruleName() { return "NoBrokenTransitions"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Set<UUID> nodeIds = graph.nodes().stream().map(n -> n.id()).collect(Collectors.toSet());
        List<ValidationError> errors = new ArrayList<>();
        for (var t : graph.transitions()) {
            if (!nodeIds.contains(t.sourceNodeId())) {
                errors.add(new ValidationError(ruleName(), "Transition references non-existent source node",
                    "TRANSITION", t.id().toString()));
            }
            if (!nodeIds.contains(t.targetNodeId())) {
                errors.add(new ValidationError(ruleName(), "Transition references non-existent target node",
                    "TRANSITION", t.id().toString()));
            }
        }
        return errors;
    }
}
