package com.channel360.workflow.application.lifecycle;

import com.channel360.workflow.domain.entity.Workflow;
import com.channel360.workflow.domain.entity.WorkflowVersion;
import com.channel360.workflow.domain.enums.VersionStatus;
import com.channel360.workflow.domain.exception.WorkflowNotFoundException;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowRepository;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LifecycleQueryService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowVersionRepository versionRepository;

    public List<Workflow> getAllWorkflows() {
        return workflowRepository.findAll();
    }

    public Workflow getWorkflow(Long id) {
        return workflowRepository.findById(id)
            .orElseThrow(() -> new WorkflowNotFoundException(id));
    }

    public List<WorkflowVersion> getVersionsForWorkflow(Long workflowId) {
        return versionRepository.findByWorkflowIdOrderByVersionNumberDesc(workflowId);
    }

    public WorkflowVersion getPublishedVersion(Long workflowId) {
        return versionRepository
            .findTopByWorkflowIdAndStatusOrderByVersionNumberDesc(workflowId, VersionStatus.PUBLISHED)
            .orElse(null);
    }

    public WorkflowVersion getLatestDraft(Long workflowId) {
        return versionRepository
            .findTopByWorkflowIdAndStatusOrderByVersionNumberDesc(workflowId, VersionStatus.DRAFT)
            .orElse(null);
    }

    public boolean isPublished(Long workflowId) {
        return versionRepository.existsByWorkflowIdAndStatus(workflowId, VersionStatus.PUBLISHED);
    }
}
