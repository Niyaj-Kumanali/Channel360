package com.channel360.workflow.api.dto.designer;

import com.channel360.workflow.application.designer.model.GraphState;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record WorkflowGraphDTO(
    List<NodeDTO> nodes,
    List<TransitionDTO> transitions,
    List<AssignmentDTO> assignments
) {
    public record NodeDTO(UUID id, String name, String type, String terminalType,
                          String label, String description, GraphState state, Long entityVersion) {}
    public record TransitionDTO(UUID id, UUID sourceNodeId, UUID targetNodeId,
                                 String action, String label, Integer priority,
                                 ConditionDTO condition, GraphState state) {}
    public record AssignmentDTO(UUID id, String policy, Integer requiredApprovalCount,
                                 List<ApproverRuleDTO> rules) {}
    public record ApproverRuleDTO(UUID id, String type, String roleName, Long userId,
                                   Long regionId, String department, String dynamicProvider) {}
    public record ConditionDTO(UUID id, String type, String operator, String field,
                                String op, String value, List<ConditionDTO> children) {}
    public record SaveRequest(WorkflowGraphDTO graph, Map<UUID, GraphState> nodeStates,
                               Map<UUID, GraphState> transitionStates) {}
}
