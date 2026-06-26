package com.channel360.homepage.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record HomepagePopupRequest(
    Long id,
    @NotBlank String title,
    String description,
    String imageUrl,
    String ctaButtonText,
    String ctaUrl,
    @NotNull Integer priority,
    Boolean active,
    LocalDateTime startDate,
    LocalDateTime endDate
) {}
