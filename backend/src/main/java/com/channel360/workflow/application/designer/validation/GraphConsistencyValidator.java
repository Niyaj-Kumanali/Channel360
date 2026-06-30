package com.channel360.workflow.application.designer.validation;

import com.channel360.workflow.application.designer.model.GraphState;
import com.channel360.workflow.domain.entity.WorkflowNode;
import com.channel360.workflow.domain.entity.WorkflowTransition;
import com.channel360.workflow.domain.exception.GraphConsistencyException;
import com.channel360.workflow.domain.exception.OptimisticLockException;
import com.channel360.workflow.domain.graph.GraphNode;
import com.channel360.workflow.domain.graph.GraphTransition;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowNodeRepository;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowTransitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GraphConsistencyValidator {

    private final WorkflowNodeRepository nodeRepository;
    private final WorkflowTransitionRepository transitionRepository;

    public void validateNodes(Map<UUID, GraphState> nodeStates, Map<UUID, GraphNode> incomingNodes) {
        Map<UUID, GraphState> modified = nodeStates.entrySet().stream()
            .filter(e -> e.getValue() == GraphState.MODIFIED || e.getValue() == GraphState.DELETED)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (modified.isEmpty()) return;

        var existingNodes = nodeRepository.findByNodeUuidIn(List.copyOf(modified.keySet()))
            .stream().collect(Collectors.toMap(WorkflowNode::getNodeUuid, Function.identity()));

        for (var entry : modified.entrySet()) {
            UUID uuid = entry.getKey();
            WorkflowNode existing = existingNodes.get(uuid);
            if (existing == null) {
                throw new GraphConsistencyException("Node " + uuid + " not found in database");
            }
            if (entry.getValue() == GraphState.MODIFIED) {
                GraphNode incoming = incomingNodes.get(uuid);
                if (incoming != null && !existing.getEntityVersion().equals(incoming.entityVersion())) {
                    throw new OptimisticLockException("Node " + incoming.name()
                        + " was modified by another user");
                }
            }
        }
    }

    public void validateTransitions(Map<UUID, GraphState> transitionStates) {
        for (var entry : transitionStates.entrySet()) {
            if (entry.getValue() == GraphState.DELETED) {
                if (!transitionRepository.findByTransitionUuid(entry.getKey()).isPresent()) {
                    throw new GraphConsistencyException("Transition " + entry.getKey() + " not found");
                }
            }
        }
    }
}
