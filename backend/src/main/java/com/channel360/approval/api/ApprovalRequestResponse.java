package com.channel360.approval.api;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ApprovalRequestResponse(
    Long id,
    Long workflowId,
    String workflowName,
    String requestType,
    Long requestReferenceId,
    Long requestRegionId,
    String requestRegionName,
    Long requestorId,
    String requestorName,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<ApprovalTaskResponse> tasks
) {}