package com.channel360.workflow.application.designer.validation.rules.transition;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class NodeWithNoOutgoingTransitionRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.TRANSITIONS; }
    @Override
    public String ruleName() { return "NodeWithNoOutgoingTransition"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Set<UUID> nodesWithOutgoing = graph.transitions().stream()
            .map(t -> t.sourceNodeId()).collect(Collectors.toSet());
        List<ValidationError> errors = new ArrayList<>();
        for (var node : graph.nodes()) {
            if (node.type() != NodeType.END && !nodesWithOutgoing.contains(node.id())) {
                errors.add(new ValidationError(ruleName(),
                    "Node '" + node.name() + "' has no outgoing transitions",
                    "NODE", node.id().toString()));
            }
        }
        return errors;
    }
}
