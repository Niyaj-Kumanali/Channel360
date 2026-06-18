package com.channel360.cms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSectionRequest {
    @NotBlank
    private String sectionName;

    @NotBlank
    private String sectionType;

    private String title;
    private String subtitle;
    private String description;
    private String imageUrl;
    private String buttonText;
    private String buttonUrl;
    private Integer displayOrder;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
