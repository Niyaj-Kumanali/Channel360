package com.channel360.approval.api;

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
public class ApprovalRequestCreate {
    @NotNull(message = "Workflow ID is required")
    private Long workflowId;

    @NotBlank(message = "Request type is required")
    private String requestType;

    private Long requestReferenceId;

    @NotNull(message = "Request region ID is required")
    private Long requestRegionId;

    @NotNull(message = "Requestor ID is required")
    private Long requestorId;
}
