package com.channel360.workflow.api.dto.facade;

public record WorkflowLookupDTO(
    Long id,
    String name,
    String description,
    String versionStatus
) {}
