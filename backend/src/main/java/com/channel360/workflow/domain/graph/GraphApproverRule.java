package com.channel360.workflow.domain.graph;

import com.channel360.workflow.domain.enums.ApproverType;
import java.util.UUID;

public record GraphApproverRule(
    UUID id,
    ApproverType type,
    String roleName,
    Long userId,
    Long regionId,
    String department,
    String dynamicProvider
) {
}
