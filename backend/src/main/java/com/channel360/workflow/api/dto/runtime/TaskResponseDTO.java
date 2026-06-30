package com.channel360.workflow.api.dto.runtime;

import java.time.LocalDateTime;

public record TaskResponseDTO(
    Long id,
    Long requestId,
    String nodeName,
    Long assignedUserId,
    String status,
    LocalDateTime createdAt
) {}
