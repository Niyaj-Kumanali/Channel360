package com.channel360.workflow.api.mapper;

import com.channel360.workflow.api.dto.designer.WorkflowGraphDTO;
import com.channel360.workflow.domain.enums.ConditionType;
import com.channel360.workflow.domain.enums.LogicalOperator;
import com.channel360.workflow.domain.graph.GraphConditionExpression;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConditionMapper {

    public GraphConditionExpression toDomain(WorkflowGraphDTO.ConditionDTO dto) {
        return new GraphConditionExpression(
            dto.id(),
            ConditionType.valueOf(dto.type()),
            dto.operator() != null ? LogicalOperator.valueOf(dto.operator()) : null,
            dto.field(), dto.op(), dto.value(),
            dto.children() != null
                ? dto.children().stream().map(this::toDomain).toList()
                : new ArrayList<>()
        );
    }

    public WorkflowGraphDTO.ConditionDTO toDTO(GraphConditionExpression expr) {
        return new WorkflowGraphDTO.ConditionDTO(
            expr.id(),
            expr.type().name(),
            expr.operator() != null ? expr.operator().name() : null,
            expr.field(), expr.op(), expr.value(),
            expr.children() != null && !expr.children().isEmpty()
                ? expr.children().stream().map(this::toDTO).toList()
                : null
        );
    }
}
