package com.channel360.workflow.application.lifecycle;

import com.channel360.workflow.application.serialization.JsonSerializer;
import com.channel360.workflow.domain.entity.Workflow;
import com.channel360.workflow.domain.entity.WorkflowVersion;
import com.channel360.workflow.domain.enums.VersionStatus;
import com.channel360.workflow.domain.exception.WorkflowNotFoundException;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowRepository;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowCloneService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowVersionRepository versionRepository;
    private final JsonSerializer jsonSerializer;

    @Transactional
    public WorkflowVersion cloneWorkflow(Long sourceWorkflowId, String newName) {
        Workflow source = workflowRepository.findById(sourceWorkflowId)
            .orElseThrow(() -> new WorkflowNotFoundException(sourceWorkflowId));

        Workflow clone = workflowRepository.save(Workflow.builder()
            .name(newName != null ? newName : source.getName() + " (Copy)")
            .description(source.getDescription())
            .active(false)
            .build());

        WorkflowVersion sourceVersion = versionRepository
            .findTopByWorkflowIdAndStatusOrderByVersionNumberDesc(sourceWorkflowId, VersionStatus.PUBLISHED)
            .orElse(null);

        if (sourceVersion != null) {
            WorkflowVersion draftVersion = WorkflowVersion.builder()
                .workflow(clone)
                .versionNumber(1)
                .status(VersionStatus.DRAFT)
                .graphJson(sourceVersion.getGraphJson())
                .build();
            return versionRepository.save(draftVersion);
        }

        WorkflowVersion draftVersion = WorkflowVersion.builder()
            .workflow(clone)
            .versionNumber(1)
            .status(VersionStatus.DRAFT)
            .graphJson("{\"nodes\":[],\"transitions\":[],\"assignments\":[]}")
            .build();
        return versionRepository.save(draftVersion);
    }
}
