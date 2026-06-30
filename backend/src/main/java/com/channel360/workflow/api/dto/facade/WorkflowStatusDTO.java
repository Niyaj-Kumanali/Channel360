package com.channel360.workflow.api.dto.facade;

public record WorkflowStatusDTO(
    Long requestId,
    String status,
    String currentNode
) {}
