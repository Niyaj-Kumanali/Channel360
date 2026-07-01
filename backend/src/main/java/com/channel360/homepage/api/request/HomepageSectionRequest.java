package com.channel360.homepage.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record HomepageSectionRequest(
    Long id,
    @NotBlank String sectionName,
    @NotBlank @Pattern(regexp = "^(hero_banner|product_journey|platform_capabilities|benefits|contact|footer|announcement|info_block|promotion|image_card|faq)$",
             message = "Invalid section type. Must be one of: hero_banner, product_journey, platform_capabilities, benefits, contact, footer, announcement, info_block, promotion, image_card, faq")
    String sectionType,
    @NotBlank String title,
    String subtitle,
    String description,
    String imageUrl,
    String buttonText,
    String buttonUrl,
    @NotNull Integer displayOrder,
    Boolean active,
    LocalDateTime startDate,
    LocalDateTime endDate
) {}
