package com.channel360.homepage.api.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record HomepageSectionResponse(
    Long id,
    String sectionName,
    String sectionType,
    String title,
    String subtitle,
    String description,
    String imageUrl,
    String buttonText,
    String buttonUrl,
    Integer displayOrder,
    Boolean active,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String createdBy,
    LocalDateTime createdAt,
    String updatedBy,
    LocalDateTime updatedAt
) {}