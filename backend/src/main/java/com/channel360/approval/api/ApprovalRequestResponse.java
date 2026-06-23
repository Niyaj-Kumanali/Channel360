package com.channel360.approval.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestResponse {
    private Long id;
    private Long workflowId;
    private String workflowName;
    private String requestType;
    private Long requestReferenceId;
    private Long requestRegionId;
    private String requestRegionName;
    private Long requestorId;
    private String requestorName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ApprovalTaskResponse> tasks;
}
