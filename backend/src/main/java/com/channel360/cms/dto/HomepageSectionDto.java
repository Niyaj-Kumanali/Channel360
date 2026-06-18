package com.channel360.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomepageSectionDto {
    private Long id;
    private String sectionName;
    private String sectionType;
    private String title;
    private String subtitle;
    private String description;
    private String imageUrl;
    private String buttonText;
    private String buttonUrl;
    private Integer displayOrder;
    private boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
