package com.channel360.approval.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ApprovalRequestCreate(
    @NotNull(message = "Workflow ID is required") Long workflowId,
    @NotBlank(message = "Request type is required") String requestType,
    Long requestReferenceId,
    @NotNull(message = "Request region ID is required") Long requestRegionId,
    @NotNull(message = "Requestor ID is required") Long requestorId
) {}
