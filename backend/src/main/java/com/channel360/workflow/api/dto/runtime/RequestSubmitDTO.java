package com.channel360.workflow.api.dto.runtime;

import java.util.Map;

public record RequestSubmitDTO(
    Long workflowId,
    String requestType,
    Map<String, Object> businessContext,
    String idempotencyKey
) {}
