package com.channel360.workflow.application.designer.validation.rules.assignment;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EveryApprovalNodeHasAssignmentRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.ASSIGNMENTS; }
    @Override
    public String ruleName() { return "EveryApprovalNodeHasAssignment"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Set<UUID> assignedNodeIds = graph.assignments().stream()
            .map(a -> {
                var ref = graph.nodes().stream().filter(n -> n.id().equals(a.id())).findFirst();
                return ref.map(n -> n.id()).orElse(null);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        List<ValidationError> errors = new ArrayList<>();
        for (var node : graph.nodes()) {
            if (node.type() == NodeType.APPROVAL && !assignedNodeIds.contains(node.id())) {
                errors.add(new ValidationError(ruleName(),
                    "APPROVAL node '" + node.name() + "' has no assignment configuration",
                    "NODE", node.id().toString()));
            }
        }
        return errors;
    }
}
