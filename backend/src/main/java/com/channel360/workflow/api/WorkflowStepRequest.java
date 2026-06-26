package com.channel360.workflow.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record WorkflowStepRequest(
    Long id,
    @NotNull(message = "Workflow ID is required") Long workflowId,
    @NotNull(message = "Step order is required") Integer stepOrder,
    @NotBlank(message = "Role name is required") String roleName,
    @NotBlank(message = "Label is required") String label,
    Boolean mandatory,
    Integer slaHours,
    String escalationRole,
    String description
) {}
