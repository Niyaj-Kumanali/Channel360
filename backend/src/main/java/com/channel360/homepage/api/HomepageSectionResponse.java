package com.channel360.homepage.api;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class HomepageSectionResponse {
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
    private Boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
