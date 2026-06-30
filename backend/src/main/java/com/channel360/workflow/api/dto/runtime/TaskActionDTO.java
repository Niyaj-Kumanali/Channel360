package com.channel360.workflow.api.dto.runtime;

import java.util.Map;

public record TaskActionDTO(
    String action,
    String comment,
    Long delegatedUserId,
    Map<String, Object> businessContext,
    String idempotencyKey
) {}
