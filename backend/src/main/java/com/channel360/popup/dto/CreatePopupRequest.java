package com.channel360.popup.dto;

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
public class CreatePopupRequest {
    @NotBlank
    private String title;

    private String description;
    private String imageUrl;
    private String ctaButtonText;
    private String ctaUrl;
    private Integer priority;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
