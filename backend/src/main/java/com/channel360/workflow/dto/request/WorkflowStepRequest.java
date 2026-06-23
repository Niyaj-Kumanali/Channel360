package com.channel360.workflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStepRequest {

    private Long id;

    @NotNull(message = "Workflow ID is required")
    private Long workflowId;

    @NotNull(message = "Step order is required")
    private Integer stepOrder;

    @NotBlank(message = "Role name is required")
    private String roleName;

    @NotBlank(message = "Label is required")
    private String label;

    private Boolean mandatory;

    private Integer slaHours;

    private String escalationRole;

    private String description;
}
