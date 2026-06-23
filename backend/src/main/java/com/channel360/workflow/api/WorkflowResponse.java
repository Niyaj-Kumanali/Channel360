package com.channel360.workflow.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponse {

    private Long id;
    private String name;
    private String description;
    private String module;
    private Boolean active;
    private String createdBy;
    private String updatedBy;
    private List<WorkflowStepResponse> steps;
}
