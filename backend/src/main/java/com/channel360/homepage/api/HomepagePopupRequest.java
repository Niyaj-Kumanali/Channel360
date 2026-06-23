package com.channel360.homepage.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HomepagePopupRequest {

    private Long id;

    @NotBlank
    private String title;

    private String description;
    private String imageUrl;
    private String ctaButtonText;
    private String ctaUrl;

    @NotNull
    private Integer priority;

    private Boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
