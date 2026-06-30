package com.channel360.workflow.application.designer.validation.rules.transition;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.graph.GraphTransition;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DuplicateActionRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.TRANSITIONS; }
    @Override
    public String ruleName() { return "DuplicateAction"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        List<ValidationError> errors = new ArrayList<>();
        Map<UUID, Map<TransitionAction, Long>> bySource = graph.transitions().stream()
            .collect(Collectors.groupingBy(
                GraphTransition::sourceNodeId,
                Collectors.groupingBy(GraphTransition::action, Collectors.counting())));
        for (Map.Entry<UUID, Map<TransitionAction, Long>> sourceEntry : bySource.entrySet()) {
            for (Map.Entry<TransitionAction, Long> actionEntry : sourceEntry.getValue().entrySet()) {
                if (actionEntry.getValue() > 1) {
                    errors.add(new ValidationError(ruleName(),
                        "Multiple transitions with action " + actionEntry.getKey()
                        + " from node " + sourceEntry.getKey() + " — conditions required",
                        "NODE", sourceEntry.getKey().toString()));
                }
            }
        }
        return errors;
    }
}
