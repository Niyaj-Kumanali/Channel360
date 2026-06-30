package com.channel360.workflow.api.facade;

import com.channel360.workflow.api.dto.WorkflowResponse;
import com.channel360.workflow.api.dto.WorkflowStepResponse;
import com.channel360.workflow.domain.ApprovalWorkflow;
import com.channel360.workflow.domain.ApprovalWorkflowStep;
import com.channel360.workflow.infrastructure.LegacyWorkflowRepository;
import com.channel360.workflow.infrastructure.WorkflowStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("legacyWorkflowFacadeImpl")
@RequiredArgsConstructor
public class LegacyWorkflowFacadeImpl implements LegacyWorkflowFacade {

    private final LegacyWorkflowRepository workflowRepository;
    private final WorkflowStepRepository workflowStepRepository;

    @Override
    public WorkflowResponse getById(Long id) {
        ApprovalWorkflow workflow = workflowRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found with id: " + id));
        return toResponse(workflow);
    }

    @Override
    public String getWorkflowNameById(Long id) {
        return workflowRepository.findActiveById(id)
                .map(ApprovalWorkflow::getName)
                .orElse(null);
    }

    @Override
    public List<WorkflowStepResponse> getStepsByWorkflowId(Long workflowId) {
        List<ApprovalWorkflowStep> steps = workflowStepRepository
                .findByWorkflowIdAndDeletedFlagFalseOrderByStepOrder(workflowId);
        return steps.stream()
                .map(this::toStepResponse)
                .toList();
    }

    @Override
    public WorkflowStepResponse getStepById(Long id) {
        ApprovalWorkflowStep step = workflowStepRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Workflow step not found with id: " + id));
        return toStepResponse(step);
    }

    private WorkflowResponse toResponse(ApprovalWorkflow workflow) {
        return WorkflowResponse.builder()
                .id(workflow.getId())
                .name(workflow.getName())
                .description(workflow.getDescription())
                .module(workflow.getModule())
                .active(workflow.getActive())
                .build();
    }

    private WorkflowStepResponse toStepResponse(ApprovalWorkflowStep step) {
        return WorkflowStepResponse.builder()
                .id(step.getId())
                .workflowId(step.getWorkflowId())
                .stepOrder(step.getStepOrder())
                .roleName(step.getRoleName())
                .label(step.getLabel())
                .mandatory(step.getMandatory())
                .slaHours(step.getSlaHours())
                .escalationRole(step.getEscalationRole())
                .description(step.getDescription())
                .build();
    }
}
