package com.channel360.workflow.domain.model;

import java.util.List;

public record SimulateResult(
    ExecutionResult finalResult,
    List<ExecutionTraceStep> trace
) {
}
