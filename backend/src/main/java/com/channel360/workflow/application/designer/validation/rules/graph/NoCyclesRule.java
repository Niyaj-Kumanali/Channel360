package com.channel360.workflow.application.designer.validation.rules.graph;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class NoCyclesRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.GRAPH; }
    @Override
    public String ruleName() { return "NoCycles"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Set<UUID> nodeIds = graph.nodes().stream().map(n -> n.id()).collect(java.util.stream.Collectors.toSet());
        Map<UUID, List<UUID>> adj = new HashMap<>();
        for (var node : graph.nodes()) adj.put(node.id(), new ArrayList<>());
        for (var t : graph.transitions()) {
            adj.get(t.sourceNodeId()).add(t.targetNodeId());
        }

        Set<UUID> white = new HashSet<>(nodeIds);
        Set<UUID> grey = new HashSet<>();
        Set<UUID> black = new HashSet<>();

        List<ValidationError> errors = new ArrayList<>();
        for (UUID id : nodeIds) {
            if (white.contains(id) && hasCycle(id, adj, white, grey, black)) {
                errors.add(new ValidationError(ruleName(), "Workflow graph contains a cycle"));
                break;
            }
        }
        return errors;
    }

    private boolean hasCycle(UUID node, Map<UUID, List<UUID>> adj,
                             Set<UUID> white, Set<UUID> grey, Set<UUID> black) {
        white.remove(node);
        grey.add(node);
        for (UUID neighbor : adj.getOrDefault(node, List.of())) {
            if (black.contains(neighbor)) continue;
            if (grey.contains(neighbor)) return true;
            if (hasCycle(neighbor, adj, white, grey, black)) return true;
        }
        grey.remove(node);
        black.add(node);
        return false;
    }
}
