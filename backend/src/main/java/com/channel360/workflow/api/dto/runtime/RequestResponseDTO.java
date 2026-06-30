package com.channel360.workflow.api.dto.runtime;

import java.time.LocalDateTime;

public record RequestResponseDTO(
    Long id,
    Long workflowVersionId,
    String requestType,
    Long requestorId,
    String status,
    LocalDateTime createdAt
) {}
