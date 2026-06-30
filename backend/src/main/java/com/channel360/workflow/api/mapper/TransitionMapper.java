package com.channel360.workflow.api.mapper;

import com.channel360.workflow.api.dto.designer.WorkflowGraphDTO;
import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.graph.GraphConditionExpression;
import com.channel360.workflow.domain.graph.GraphTransition;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TransitionMapper {

    private final ConditionMapper conditionMapper;

    public TransitionMapper(ConditionMapper conditionMapper) {
        this.conditionMapper = conditionMapper;
    }

    public GraphTransition toDomain(WorkflowGraphDTO.TransitionDTO dto) {
        return new GraphTransition(
            dto.id(), dto.sourceNodeId(), dto.targetNodeId(),
            TransitionAction.valueOf(dto.action()), dto.label(),
            dto.priority() != null ? dto.priority() : 0,
            dto.condition() != null ? conditionMapper.toDomain(dto.condition()) : null
        );
    }

    public WorkflowGraphDTO.TransitionDTO toDTO(GraphTransition transition) {
        return new WorkflowGraphDTO.TransitionDTO(
            transition.id(), transition.sourceNodeId(), transition.targetNodeId(),
            transition.action().name(), transition.label(), transition.priority(),
            transition.condition() != null ? conditionMapper.toDTO(transition.condition()) : null,
            null
        );
    }
}
