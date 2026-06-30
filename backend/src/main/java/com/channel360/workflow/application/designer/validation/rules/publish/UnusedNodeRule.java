package com.channel360.workflow.application.designer.validation.rules.publish;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class UnusedNodeRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.PUBLISH; }
    @Override
    public String ruleName() { return "UnusedNode"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Set<UUID> referenced = new HashSet<>();
        for (var t : graph.transitions()) {
            referenced.add(t.sourceNodeId());
            referenced.add(t.targetNodeId());
        }
        referenced.addAll(graph.assignments().stream().map(a -> a.id()).toList());

        List<ValidationError> errors = new ArrayList<>();
        for (var node : graph.nodes()) {
            if (!referenced.contains(node.id()) && node.type() != NodeType.START) {
                errors.add(new ValidationError(ruleName(),
                    "Node '" + node.name() + "' is not referenced by any transition or assignment",
                    "NODE", node.id().toString()));
            }
        }
        return errors;
    }
}
