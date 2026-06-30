package com.channel360.workflow.application.designer.validation.rules.condition;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.ConditionType;
import com.channel360.workflow.domain.graph.GraphConditionExpression;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UnusedConditionRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.CONDITIONS; }
    @Override
    public String ruleName() { return "UnusedCondition"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Set<UUID> referencedIds = graph.transitions().stream()
            .filter(t -> t.condition() != null)
            .flatMap(t -> collectConditionIds(t.condition()).stream())
            .collect(Collectors.toSet());

        List<ValidationError> errors = new ArrayList<>();
        for (var node : graph.nodes()) {
            var assignment = graph.assignments().stream()
                .filter(a -> a.id().equals(node.id())).findFirst();
            if (assignment.isPresent()) {
                for (var rule : assignment.get().rules()) {
                    if (rule.dynamicProvider() != null) {
                        errors.add(new ValidationError(ruleName(),
                            "Assignment rule references dynamic provider '" + rule.dynamicProvider()
                            + "' but no transition uses a condition that evaluates it",
                            "APPROVER_RULE", rule.id().toString()));
                    }
                }
            }
        }
        return errors;
    }

    private Set<UUID> collectConditionIds(GraphConditionExpression expr) {
        Set<UUID> ids = new HashSet<>();
        ids.add(expr.id());
        if (expr.children() != null) {
            for (var child : expr.children()) {
                ids.addAll(collectConditionIds(child));
            }
        }
        return ids;
    }
}
