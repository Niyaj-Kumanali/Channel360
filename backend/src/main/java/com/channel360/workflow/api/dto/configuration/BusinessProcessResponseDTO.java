package com.channel360.workflow.api.dto.configuration;

public record BusinessProcessResponseDTO(
    Long id,
    String name,
    String code,
    String description,
    boolean active
) {}
