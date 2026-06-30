package com.channel360.workflow.api.mapper;

import com.channel360.workflow.api.dto.designer.WorkflowGraphDTO;
import com.channel360.workflow.domain.enums.ApproverType;
import com.channel360.workflow.domain.enums.AssignmentPolicy;
import com.channel360.workflow.domain.graph.GraphApproverRule;
import com.channel360.workflow.domain.graph.GraphAssignment;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {

    public GraphAssignment toDomain(WorkflowGraphDTO.AssignmentDTO dto) {
        return new GraphAssignment(
            dto.id(),
            AssignmentPolicy.valueOf(dto.policy()),
            dto.requiredApprovalCount(),
            dto.rules().stream()
                .map(this::toRuleDomain)
                .toList()
        );
    }

    public WorkflowGraphDTO.AssignmentDTO toDTO(GraphAssignment assignment) {
        return new WorkflowGraphDTO.AssignmentDTO(
            assignment.id(),
            assignment.policy().name(),
            assignment.requiredApprovalCount(),
            assignment.rules().stream()
                .map(this::toRuleDTO)
                .toList()
        );
    }

    private GraphApproverRule toRuleDomain(WorkflowGraphDTO.ApproverRuleDTO dto) {
        return new GraphApproverRule(
            dto.id(), ApproverType.valueOf(dto.type()),
            dto.roleName(), dto.userId(), dto.regionId(),
            dto.department(), dto.dynamicProvider()
        );
    }

    private WorkflowGraphDTO.ApproverRuleDTO toRuleDTO(GraphApproverRule rule) {
        return new WorkflowGraphDTO.ApproverRuleDTO(
            rule.id(), rule.type().name(), rule.roleName(),
            rule.userId(), rule.regionId(), rule.department(),
            rule.dynamicProvider()
        );
    }
}
