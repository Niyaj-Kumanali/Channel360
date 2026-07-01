package com.channel360.homepage.api;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record HomepagePopupResponse(
    Long id,
    String title,
    String description,
    String imageUrl,
    String ctaButtonText,
    String ctaUrl,
    Integer priority,
    Boolean active,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String createdBy,
    LocalDateTime createdAt,
    String updatedBy,
    LocalDateTime updatedAt
) {}