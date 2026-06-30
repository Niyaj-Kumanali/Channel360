package com.channel360.workflow.api.dto.lifecycle;

import com.channel360.workflow.domain.enums.VersionStatus;

public record VersionResponseDTO(
    Long id,
    Long workflowId,
    Integer versionNumber,
    VersionStatus status
) {}
