package com.channel360.approval.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalTaskResponse {
    private Long id;
    private Long approvalRequestId;
    private Long workflowStepId;
    private String stepLabel;
    private Integer stepOrder;
    private Long assignedRoleId;
    private String assignedRoleName;
    private Long assignedUserId;
    private String assignedUserName;
    private Long assignedRegionId;
    private String assignedRegionName;
    private String status;
    private Long approvedBy;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private Long rejectedBy;
    private String rejectedByName;
    private LocalDateTime rejectedAt;
    private String comments;
    private LocalDateTime createdAt;
}
