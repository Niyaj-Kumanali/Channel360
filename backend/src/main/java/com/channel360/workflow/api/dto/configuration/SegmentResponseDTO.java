package com.channel360.workflow.api.dto.configuration;

public record SegmentResponseDTO(
    Long id,
    String name,
    String code,
    String description,
    boolean active
) {}
