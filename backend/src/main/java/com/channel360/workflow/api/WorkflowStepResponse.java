package com.channel360.workflow.api;

import lombok.Builder;

@Builder
public record WorkflowStepResponse(
    Long id,
    Long workflowId,
    Integer stepOrder,
    String roleName,
    String label,
    Boolean mandatory,
    Integer slaHours,
    String escalationRole,
    String description,
    String createdBy,
    String updatedBy
) {}