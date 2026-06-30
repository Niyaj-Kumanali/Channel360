package com.channel360.workflow.application.designer.model;

import com.channel360.workflow.domain.graph.WorkflowGraph;
import java.util.Map;
import java.util.UUID;

public record SaveWorkflowCommand(
    Long workflowId,
    WorkflowGraph graph,
    Map<UUID, GraphState> nodeStates,
    Map<UUID, GraphState> transitionStates
) {
}
