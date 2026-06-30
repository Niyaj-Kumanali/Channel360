package com.channel360.workflow.api.dto.runtime;

import java.time.LocalDateTime;

public record TimelineResponseDTO(
    Long id,
    String action,
    Long actorId,
    String comments,
    LocalDateTime createdAt
) {}
