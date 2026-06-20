package com.channel360.homepage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HomepageSectionRequest {

    private Long id;

    @NotBlank
    private String sectionName;

    @NotBlank
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
