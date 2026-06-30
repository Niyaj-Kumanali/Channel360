package com.channel360.workflow.application.designer.validation.rules.transition;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.graph.GraphTransition;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DeterministicTransitionRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.TRANSITIONS; }
    @Override
    public String ruleName() { return "DeterministicTransition"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        List<ValidationError> errors = new ArrayList<>();
        Map<UUID, List<GraphTransition>> bySource = graph.transitions().stream()
            .collect(Collectors.groupingBy(GraphTransition::sourceNodeId));
        for (Map.Entry<UUID, List<GraphTransition>> entry : bySource.entrySet()) {
            Map<TransitionAction, List<GraphTransition>> byAction = entry.getValue().stream()
                .collect(Collectors.groupingBy(GraphTransition::action));
            for (Map.Entry<TransitionAction, List<GraphTransition>> actionEntry : byAction.entrySet()) {
                long conditional = actionEntry.getValue().stream()
                    .filter(t -> t.condition() != null).count();
                long unconditional = actionEntry.getValue().stream()
                    .filter(t -> t.condition() == null).count();
                if (conditional > 0 && unconditional > 1) {
                    errors.add(new ValidationError(ruleName(),
                        "Non-deterministic transitions for action " + actionEntry.getKey()
                        + " from node " + entry.getKey()
                        + ": multiple unconditional transitions",
                        "NODE", entry.getKey().toString()));
                }
            }
        }
        return errors;
    }
}
