package com.channel360.workflow.application.designer.validation.rules.transition;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.ConditionType;
import com.channel360.workflow.domain.graph.GraphConditionExpression;
import com.channel360.workflow.domain.graph.GraphTransition;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TransitionOverlapRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.TRANSITIONS; }
    @Override
    public String ruleName() { return "TransitionOverlap"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Map<UUID, List<GraphTransition>> bySource = graph.transitions().stream()
            .collect(Collectors.groupingBy(GraphTransition::sourceNodeId));
        List<ValidationError> errors = new ArrayList<>();
        for (Map.Entry<UUID, List<GraphTransition>> entry : bySource.entrySet()) {
            List<GraphTransition> transitions = entry.getValue();
            for (int i = 0; i < transitions.size(); i++) {
                for (int j = i + 1; j < transitions.size(); j++) {
                    GraphTransition a = transitions.get(i);
                    GraphTransition b = transitions.get(j);
                    if (a.action() == b.action() && hasOverlap(a.condition(), b.condition())) {
                        errors.add(new ValidationError(ruleName(),
                            "Overlapping conditions for transitions with action " + a.action()
                            + " from node " + entry.getKey(),
                            "TRANSITION", a.id().toString()));
                    }
                }
            }
        }
        return errors;
    }

    private boolean hasOverlap(GraphConditionExpression a, GraphConditionExpression b) {
        if (a == null || b == null) return true;
        if (a.type() == ConditionType.GROUP || b.type() == ConditionType.GROUP) return false;
        return a.field().equals(b.field()) && a.op().equals(b.op());
    }
}
