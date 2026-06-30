package com.channel360.workflow.application.lifecycle;

import com.channel360.workflow.application.designer.validation.ValidationResult;
import com.channel360.workflow.application.designer.validation.WorkflowValidator;
import com.channel360.workflow.application.serialization.JsonSerializer;
import com.channel360.workflow.domain.entity.Workflow;
import com.channel360.workflow.domain.entity.WorkflowVersion;
import com.channel360.workflow.domain.enums.VersionStatus;
import com.channel360.workflow.domain.exception.WorkflowInUseException;
import com.channel360.workflow.domain.exception.WorkflowNotFoundException;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowRepository;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowRequestRepository;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowVersionRepository;
import com.channel360.workflow.infrastructure.workflowdefinition.CachedWorkflowDefinitionProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowLifecycleService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowVersionRepository versionRepository;
    private final WorkflowRequestRepository requestRepository;
    private final WorkflowValidator workflowValidator;
    private final JsonSerializer jsonSerializer;
    private final CachedWorkflowDefinitionProvider definitionProvider;

    @Transactional
    public WorkflowVersion createWorkflow(String name, String description) {
        Workflow workflow = workflowRepository.save(Workflow.builder()
            .name(name).description(description).build());
        return createDraft(workflow);
    }

    @Transactional
    public WorkflowVersion createDraft(Long workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
            .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
        return createDraft(workflow);
    }

    @Transactional
    public void publish(Long versionId) {
        WorkflowVersion version = versionRepository.findById(versionId)
            .orElseThrow(() -> new WorkflowNotFoundException("Version not found: " + versionId));

        WorkflowGraph graph = jsonSerializer.fromJson(version.getGraphJson(), WorkflowGraph.class);
        ValidationResult validation = workflowValidator.validate(graph);
        if (!validation.valid()) {
            throw new com.channel360.workflow.domain.exception.WorkflowValidationException(
                validation.errors().stream().map(e -> e.message()).toList());
        }

        version.setStatus(VersionStatus.PUBLISHED);
        versionRepository.save(version);
        definitionProvider.onPublish(versionId);
    }

    @Transactional
    public void archive(Long versionId) {
        WorkflowVersion version = versionRepository.findById(versionId)
            .orElseThrow(() -> new WorkflowNotFoundException("Version not found: " + versionId));
        version.setStatus(VersionStatus.ARCHIVED);
        versionRepository.save(version);
        definitionProvider.evict(versionId);
    }

    @Transactional
    public void activate(Long workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
            .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
        workflow.setActive(true);
        workflowRepository.save(workflow);
    }

    @Transactional
    public void deactivate(Long workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
            .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
        if (requestRepository.existsByWorkflowVersionIdAndStatus(workflowId,
                com.channel360.workflow.domain.enums.RequestStatus.IN_PROGRESS)) {
            throw new WorkflowInUseException(workflowId);
        }
        workflow.setActive(false);
        workflowRepository.save(workflow);
    }

    private WorkflowVersion createDraft(Workflow workflow) {
        int nextVersion = versionRepository.findByWorkflowIdOrderByVersionNumberDesc(workflow.getId())
            .stream().findFirst().map(v -> v.getVersionNumber() + 1).orElse(1);

        WorkflowVersion version = versionRepository.save(WorkflowVersion.builder()
            .workflow(workflow).versionNumber(nextVersion)
            .status(VersionStatus.DRAFT).graphJson("{\"nodes\":[],\"transitions\":[],\"assignments\":[]}")
            .build());
        return version;
    }
}
