package com.channel360.workflow.application;

import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.workflow.api.dto.WorkflowRequest;
import com.channel360.workflow.api.dto.WorkflowStepRequest;
import com.channel360.workflow.api.dto.WorkflowResponse;
import com.channel360.workflow.api.dto.WorkflowStepResponse;
import com.channel360.workflow.domain.ApprovalWorkflow;
import com.channel360.workflow.domain.ApprovalWorkflowStep;
import com.channel360.workflow.domain.event.WorkflowCreatedEvent;
import com.channel360.workflow.infrastructure.LegacyWorkflowRepository;
import com.channel360.workflow.infrastructure.WorkflowStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkflowService {

    private final LegacyWorkflowRepository workflowRepository;
    private final WorkflowStepRepository stepRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<WorkflowResponse> getAllWorkflows() {
        return workflowRepository.findByDeletedFlagFalseOrderByModuleAscNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    public WorkflowResponse getWorkflowById(Long id) {
        return workflowRepository.findActiveById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));
    }

    @Transactional
    public WorkflowResponse createWorkflow(WorkflowRequest request, String user) {
        workflowRepository.spSave(null, request.name(), request.description(),
                request.module(), request.active(), user);

        ApprovalWorkflow saved = workflowRepository.findActiveByName(request.name())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "name", request.name()));
        eventPublisher.publishEvent(new WorkflowCreatedEvent(saved));
        return toDto(saved);
    }

    @Transactional
    public WorkflowResponse updateWorkflow(Long id, WorkflowRequest request, String user) {
        workflowRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));

        workflowRepository.spSave(id, request.name(), request.description(),
                request.module(), request.active(), user);

        return workflowRepository.findActiveById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));
    }

    @Transactional
    public void deleteWorkflow(Long id) {
        if (!workflowRepository.existsById(id)) {
            throw new ResourceNotFoundException("Workflow", "id", id);
        }
        workflowRepository.spDelete(id);
    }

    @Transactional
    public WorkflowStepResponse addStep(WorkflowStepRequest request, String user) {
        workflowRepository.findActiveById(request.workflowId())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", request.workflowId()));

        stepRepository.spSave(null, request.workflowId(), request.stepOrder(),
                request.roleName(), request.label(), request.mandatory(),
                request.slaHours(), request.escalationRole(), request.description(), user);

        ApprovalWorkflowStep saved = stepRepository
                .findActiveByWorkflowIdAndStepOrderAndLabelAndRoleName(
                        request.workflowId(), request.stepOrder(),
                        request.label(), request.roleName())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow step", "workflowId", request.workflowId()));
        return toStepDto(saved);
    }

    @Transactional
    public WorkflowStepResponse updateStep(Long stepId, WorkflowStepRequest request, String user) {
        stepRepository.findActiveById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow step", "id", stepId));

        stepRepository.spSave(stepId, request.workflowId(), request.stepOrder(),
                request.roleName(), request.label(), request.mandatory(),
                request.slaHours(), request.escalationRole(), request.description(), user);

        return stepRepository.findActiveById(stepId)
                .map(this::toStepDto)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow step", "id", stepId));
    }

    @Transactional
    public void deleteStep(Long stepId) {
        if (!stepRepository.existsById(stepId)) {
            throw new ResourceNotFoundException("Workflow step", "id", stepId);
        }
        stepRepository.spDelete(stepId);
    }

    private WorkflowResponse toDto(ApprovalWorkflow wf) {
        List<WorkflowStepResponse> steps = stepRepository
                .findByWorkflowIdAndDeletedFlagFalseOrderByStepOrder(wf.getId())
                .stream().map(this::toStepDto).toList();

        return WorkflowResponse.builder()
                .id(wf.getId())
                .name(wf.getName())
                .description(wf.getDescription())
                .module(wf.getModule())
                .active(wf.getActive())
                .createdBy(wf.getCreatedBy())
                .updatedBy(wf.getUpdatedBy())
                .steps(steps)
                .build();
    }

    private WorkflowStepResponse toStepDto(ApprovalWorkflowStep step) {
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
                .createdBy(step.getCreatedBy())
                .updatedBy(step.getUpdatedBy())
                .build();
    }
}
