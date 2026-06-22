package com.channel360.homepage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HomepageSectionRequest {

    private Long id;

    @NotBlank
    private String sectionName;

    @NotBlank
    @Pattern(regexp = "^(hero_banner|product_journey|platform_capabilities|benefits|contact|footer|announcement|info_block|promotion|image_card|faq)$",
             message = "Invalid section type. Must be one of: hero_banner, product_journey, platform_capabilities, benefits, contact, footer, announcement, info_block, promotion, image_card, faq")
    private String sectionType;

    @NotBlank
    private String title;

    private String subtitle;
    private String description;
    private String imageUrl;
    private String buttonText;
    private String buttonUrl;

    @NotNull
    private Integer displayOrder;

    private Boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
