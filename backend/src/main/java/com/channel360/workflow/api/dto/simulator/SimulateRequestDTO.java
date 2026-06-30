package com.channel360.workflow.api.dto.simulator;

import java.util.Map;

public record SimulateRequestDTO(
    Long workflowVersionId,
    Map<String, Object> businessContext
) {}
