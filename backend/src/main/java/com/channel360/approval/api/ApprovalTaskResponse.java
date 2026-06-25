package com.channel360.approval.api;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ApprovalTaskResponse(
    Long id,
    Long approvalRequestId,
    Long workflowStepId,
    String stepLabel,
    Integer stepOrder,
    Long assignedRoleId,
    String assignedRoleName,
    Long assignedUserId,
    String assignedUserName,
    Long assignedRegionId,
    String assignedRegionName,
    String status,
    Long approvedBy,
    String approvedByName,
    LocalDateTime approvedAt,
    Long rejectedBy,
    String rejectedByName,
    LocalDateTime rejectedAt,
    String comments,
    LocalDateTime createdAt
) {}