package com.channel360.workflow.application.designer.validation.rules.transition;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.graph.GraphTransition;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TransitionGapRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.TRANSITIONS; }
    @Override
    public String ruleName() { return "TransitionGap"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        List<ValidationError> errors = new ArrayList<>();
        Map<UUID, List<GraphTransition>> bySource = graph.transitions().stream()
            .collect(Collectors.groupingBy(GraphTransition::sourceNodeId));
        for (Map.Entry<UUID, List<GraphTransition>> entry : bySource.entrySet()) {
            boolean hasDefault = entry.getValue().stream()
                .anyMatch(t -> t.condition() == null);
            if (!hasDefault) {
                errors.add(new ValidationError(ruleName(),
                    "Node " + entry.getKey() + " has no default transition (unconditional fallback)",
                    "NODE", entry.getKey().toString()));
            }
        }
        return errors;
    }
}
