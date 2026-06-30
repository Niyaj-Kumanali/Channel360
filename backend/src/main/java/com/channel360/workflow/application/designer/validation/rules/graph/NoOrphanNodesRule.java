package com.channel360.workflow.application.designer.validation.rules.graph;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class NoOrphanNodesRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.REFERENCES; }
    @Override
    public String ruleName() { return "NoOrphanNodes"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Set<UUID> targetIds = graph.transitions().stream()
            .map(t -> t.targetNodeId()).collect(Collectors.toSet());
        Set<UUID> startIds = graph.nodes().stream()
            .filter(n -> n.type() == NodeType.START)
            .map(n -> n.id()).collect(Collectors.toSet());

        List<ValidationError> errors = new ArrayList<>();
        for (var node : graph.nodes()) {
            if (!startIds.contains(node.id()) && !targetIds.contains(node.id())) {
                errors.add(new ValidationError(ruleName(), "Node '" + node.name()
                    + "' has no incoming transitions", "NODE", node.id().toString()));
            }
        }
        return errors;
    }
}
