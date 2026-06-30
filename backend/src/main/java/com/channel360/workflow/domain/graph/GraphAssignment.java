package com.channel360.workflow.domain.graph;

import com.channel360.workflow.domain.enums.AssignmentPolicy;
import java.util.List;
import java.util.UUID;

public record GraphAssignment(
    UUID id,
    AssignmentPolicy policy,
    Integer requiredApprovalCount,
    List<GraphApproverRule> rules
) {
}
