package com.channel360.workflow.domain.model;

import com.channel360.workflow.domain.enums.ApproverType;

public record ResolvedAssignment(
    Long userId,
    ApproverType type
) {
}
