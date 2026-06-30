package com.channel360.workflow.api.dto.facade;

import java.time.LocalDateTime;

public record PendingApprovalSummaryDTO(
    Long taskId,
    String requestType,
    LocalDateTime createdAt
) {}
