package com.channel360.workflow.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStepResponse {

    private Long id;
    private Long workflowId;
    private Integer stepOrder;
    private String roleName;
    private String label;
    private Boolean mandatory;
    private Integer slaHours;
    private String escalationRole;
    private String description;
    private String createdBy;
    private String updatedBy;
}
