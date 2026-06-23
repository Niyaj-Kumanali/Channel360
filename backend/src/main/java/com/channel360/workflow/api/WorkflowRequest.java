package com.channel360.workflow.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRequest {

    private Long id;

    @NotBlank(message = "Workflow name is required")
    private String name;

    private String description;

    private String module;

    private Boolean active;
}
