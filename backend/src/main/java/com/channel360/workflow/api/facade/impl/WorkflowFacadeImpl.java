package com.channel360.workflow.api.facade.impl;

import com.channel360.workflow.api.dto.facade.PendingApprovalSummaryDTO;
import com.channel360.workflow.api.dto.facade.WorkflowLookupDTO;
import com.channel360.workflow.api.dto.facade.WorkflowStatusDTO;
import com.channel360.workflow.api.facade.WorkflowFacade;
import com.channel360.workflow.domain.enums.TaskStatus;
import com.channel360.workflow.domain.enums.VersionStatus;
import com.channel360.workflow.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkflowFacadeImpl implements WorkflowFacade {

    private final WorkflowRepository workflowRepository;
    private final WorkflowVersionRepository versionRepository;
    private final WorkflowTaskRepository taskRepository;
    private final WorkflowRequestRepository requestRepository;

    @Override
    public WorkflowLookupDTO findWorkflow(Long workflowId) {
        var workflow = workflowRepository.findById(workflowId).orElse(null);
        if (workflow == null) return null;
        var version = versionRepository
            .findTopByWorkflowIdAndStatusOrderByVersionNumberDesc(workflowId, VersionStatus.PUBLISHED)
            .orElse(null);
        return new WorkflowLookupDTO(workflow.getId(), workflow.getName(),
            workflow.getDescription(),
            version != null ? version.getStatus().name() : "NO_PUBLISHED_VERSION");
    }

    @Override
    public List<PendingApprovalSummaryDTO> findPendingTasks(Long userId) {
        return taskRepository.findByAssignedUserIdAndStatus(userId, TaskStatus.PENDING)
            .stream()
            .map(t -> new PendingApprovalSummaryDTO(t.getId(),
                t.getRequest().getRequestType(), t.getCreatedAt()))
            .toList();
    }

    @Override
    public boolean hasPendingApproval(Long requestId) {
        return taskRepository.countByRequestIdAndStatus(requestId, TaskStatus.PENDING) > 0;
    }

    @Override
    public String getRequestStatus(Long requestId) {
        return requestRepository.findById(requestId)
            .map(r -> r.getStatus().name())
            .orElse("NOT_FOUND");
    }
}
