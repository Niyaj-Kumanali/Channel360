package com.channel360.workflow.domain.graph;

import com.channel360.workflow.domain.enums.ConditionType;
import com.channel360.workflow.domain.enums.LogicalOperator;
import java.util.List;
import java.util.UUID;

public record GraphConditionExpression(
    UUID id,
    ConditionType type,
    LogicalOperator operator,
    String field,
    String op,
    String value,
    List<GraphConditionExpression> children
) {
}
