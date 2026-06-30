package com.channel360.workflow.domain.model;

import java.time.Instant;
import java.util.List;

public record ExecutionTraceStep(
    NodeRef fromNode,
    CompiledTransition transition,
    String conditionResult,
    List<ResolvedAssignment> approvers,
    NodeRef toNode,
    Instant timestamp
) {
}
