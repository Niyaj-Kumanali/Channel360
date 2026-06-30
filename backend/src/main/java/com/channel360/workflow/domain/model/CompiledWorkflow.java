package com.channel360.workflow.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record CompiledWorkflow(
    Long workflowVersionId,
    UUID startNodeId,
    Map<UUID, NodeRef> nodes,
    Map<UUID, List<CompiledTransition>> outgoingTransitions
) {
    public Optional<NodeRef> getNode(UUID nodeId) {
        return Optional.ofNullable(nodes.get(nodeId));
    }

    public List<CompiledTransition> getOutgoing(UUID nodeId) {
        return outgoingTransitions.getOrDefault(nodeId, Collections.emptyList());
    }

    public boolean hasNode(UUID nodeId) {
        return nodes.containsKey(nodeId);
    }
}
