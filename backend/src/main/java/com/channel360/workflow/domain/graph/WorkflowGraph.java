package com.channel360.workflow.domain.graph;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record WorkflowGraph(
    List<GraphNode> nodes,
    List<GraphTransition> transitions,
    List<GraphAssignment> assignments
) {
    public Optional<GraphNode> findNode(UUID nodeId) {
        return nodes.stream().filter(n -> n.id().equals(nodeId)).findFirst();
    }

    public List<GraphTransition> findOutgoing(UUID nodeId) {
        return transitions.stream().filter(t -> t.sourceNodeId().equals(nodeId)).toList();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
