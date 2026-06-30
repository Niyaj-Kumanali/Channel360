package com.channel360.workflow.application.designer.validation.rules.graph;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.enums.TerminalType;
import com.channel360.workflow.domain.graph.GraphNode;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class InfiniteLoopDetectorRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.GRAPH; }
    @Override
    public String ruleName() { return "InfiniteLoopDetector"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Optional<GraphNode> start = graph.nodes().stream()
            .filter(n -> n.type() == NodeType.START).findFirst();
        if (start.isEmpty()) return List.of();

        Set<UUID> terminalIds = graph.nodes().stream()
            .filter(n -> n.type() == NodeType.END)
            .map(GraphNode::id).collect(java.util.stream.Collectors.toSet());

        List<ValidationError> errors = new ArrayList<>();
        if (!canReachTerminal(start.get().id(), graph, terminalIds, new HashSet<>())) {
            errors.add(new ValidationError(ruleName(), "START node cannot reach any END node"));
        }
        return errors;
    }

    private boolean canReachTerminal(UUID nodeId, WorkflowGraph graph,
                                     Set<UUID> terminalIds, Set<UUID> visited) {
        if (terminalIds.contains(nodeId)) return true;
        if (!visited.add(nodeId)) return false;
        List<UUID> targets = graph.findOutgoing(nodeId).stream()
            .map(t -> t.targetNodeId()).toList();
        if (targets.isEmpty()) return false;
        return targets.stream().anyMatch(t -> canReachTerminal(t, graph, terminalIds, visited));
    }
}
