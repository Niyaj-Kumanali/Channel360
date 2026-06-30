package com.channel360.workflow.application.designer;

import com.channel360.workflow.domain.entity.Workflow;
import com.channel360.workflow.domain.entity.WorkflowVersion;
import com.channel360.workflow.domain.enums.VersionStatus;
import com.channel360.workflow.domain.exception.WorkflowNotFoundException;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowRepository;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DesignerQueryService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowVersionRepository versionRepository;

    public Workflow getWorkflow(Long workflowId) {
        return workflowRepository.findById(workflowId)
            .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }

    public List<Workflow> getAllWorkflows() {
        return workflowRepository.findByActiveTrue();
    }

    public WorkflowVersion getDraftVersion(Long workflowId) {
        return versionRepository
            .findTopByWorkflowIdAndStatusOrderByVersionNumberDesc(workflowId, VersionStatus.DRAFT)
            .orElseThrow(() -> new WorkflowNotFoundException("No draft version for workflow " + workflowId));
    }

    public WorkflowVersion getPublishedVersion(Long workflowId) {
        return versionRepository
            .findTopByWorkflowIdAndStatusOrderByVersionNumberDesc(workflowId, VersionStatus.PUBLISHED)
            .orElseThrow(() -> new WorkflowNotFoundException("No published version for workflow " + workflowId));
    }

    public List<WorkflowVersion> getVersions(Long workflowId) {
        return versionRepository.findByWorkflowIdOrderByVersionNumberDesc(workflowId);
    }

    public boolean hasDraft(Long workflowId) {
        return versionRepository.existsByWorkflowIdAndStatus(workflowId, VersionStatus.DRAFT);
    }
}
