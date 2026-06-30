package com.channel360.workflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record WorkflowRequest(
    Long id,
    @NotBlank(message = "Workflow name is required") String name,
    String description,
    String module,
    Boolean active
) {}
