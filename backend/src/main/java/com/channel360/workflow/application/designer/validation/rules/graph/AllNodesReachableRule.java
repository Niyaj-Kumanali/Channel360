package com.channel360.workflow.application.designer.validation.rules.graph;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AllNodesReachableRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.GRAPH; }
    @Override
    public String ruleName() { return "AllNodesReachable"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Optional<UUID> startId = graph.nodes().stream()
            .filter(n -> n.type() == NodeType.START).map(n -> n.id()).findFirst();
        if (startId.isEmpty()) return List.of();

        Set<UUID> visited = new HashSet<>();
        Deque<UUID> stack = new ArrayDeque<>();
        stack.push(startId.get());
        while (!stack.isEmpty()) {
            UUID current = stack.pop();
            if (!visited.add(current)) continue;
            graph.findOutgoing(current).stream()
                .map(t -> t.targetNodeId())
                .filter(id -> !visited.contains(id))
                .forEach(stack::push);
        }

        List<ValidationError> errors = new ArrayList<>();
        for (var node : graph.nodes()) {
            if (!visited.contains(node.id())) {
                errors.add(new ValidationError(ruleName(), "Node '" + node.name()
                    + "' is not reachable from START", "NODE", node.id().toString()));
            }
        }
        return errors;
    }
}
