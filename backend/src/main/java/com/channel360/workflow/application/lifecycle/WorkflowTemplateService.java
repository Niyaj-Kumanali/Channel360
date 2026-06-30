package com.channel360.workflow.application.lifecycle;

import com.channel360.workflow.application.serialization.JsonSerializer;
import com.channel360.workflow.domain.entity.Workflow;
import com.channel360.workflow.domain.entity.WorkflowVersion;
import com.channel360.workflow.domain.enums.VersionStatus;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowRepository;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowTemplateService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowVersionRepository versionRepository;
    private final JsonSerializer jsonSerializer;

    @Transactional
    public WorkflowVersion createFromTemplate(String name, String description, WorkflowGraph templateGraph) {
        Workflow workflow = workflowRepository.save(Workflow.builder()
            .name(name).description(description).active(false).build());

        WorkflowVersion draft = WorkflowVersion.builder()
            .workflow(workflow).versionNumber(1)
            .status(VersionStatus.DRAFT)
            .graphJson(jsonSerializer.toJson(templateGraph))
            .build();
        return versionRepository.save(draft);
    }

    public WorkflowGraph getTemplateGraph(Long workflowVersionId) {
        WorkflowVersion version = versionRepository.findById(workflowVersionId)
            .orElseThrow(() -> new RuntimeException("Version not found: " + workflowVersionId));
        return jsonSerializer.fromJson(version.getGraphJson(), WorkflowGraph.class);
    }
}
