package com.channel360.workflow.application.designer.model;

import com.channel360.workflow.domain.graph.GraphNode;
import com.channel360.workflow.domain.graph.GraphTransition;
import java.util.List;

public record SynchronizationPlan(
    List<GraphNode> nodesToCreate,
    List<GraphNode> nodesToUpdate,
    List<GraphNode> nodesToDelete,
    List<GraphTransition> transitionsToCreate,
    List<GraphTransition> transitionsToUpdate,
    List<GraphTransition> transitionsToDelete
) {
    public boolean hasChanges() {
        return !nodesToCreate.isEmpty() || !nodesToUpdate.isEmpty() || !nodesToDelete.isEmpty()
            || !transitionsToCreate.isEmpty() || !transitionsToUpdate.isEmpty() || !transitionsToDelete.isEmpty();
    }
}
