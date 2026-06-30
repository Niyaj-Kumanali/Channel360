package com.channel360.workflow.domain.model;

import com.channel360.workflow.domain.enums.AssignmentPolicy;
import java.util.List;
import java.util.UUID;

public record TaskPlan(
    UUID nodeId,
    List<ResolvedAssignment> assignees,
    AssignmentPolicy policy
) {
}
