package com.channel360.homepage.api;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class HomepagePopupResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String ctaButtonText;
    private String ctaUrl;
    private Integer priority;
    private Boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
