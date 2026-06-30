package com.channel360.workflow.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record WorkflowResponse(
    Long id,
    String name,
    String description,
    String module,
    Boolean active,
    String createdBy,
    String updatedBy,
    List<WorkflowStepResponse> steps
) {}