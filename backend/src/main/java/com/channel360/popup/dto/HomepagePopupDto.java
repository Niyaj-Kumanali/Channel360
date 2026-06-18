package com.channel360.popup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomepagePopupDto {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String ctaButtonText;
    private String ctaUrl;
    private Integer priority;
    private boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
