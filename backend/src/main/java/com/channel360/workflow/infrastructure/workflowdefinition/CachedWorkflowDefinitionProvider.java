package com.channel360.workflow.infrastructure.workflowdefinition;

import com.channel360.workflow.application.engine.condition.compiler.ConditionCompiler;
import com.channel360.workflow.application.engine.provider.WorkflowDefinitionProvider;
import com.channel360.workflow.application.serialization.JsonSerializer;
import com.channel360.workflow.domain.enums.VersionStatus;
import com.channel360.workflow.domain.exception.WorkflowNotFoundException;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import com.channel360.workflow.domain.model.CompiledWorkflow;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowVersionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class CachedWorkflowDefinitionProvider implements WorkflowDefinitionProvider {

    private final ConcurrentHashMap<Long, CompiledWorkflow> cache = new ConcurrentHashMap<>();
    private final WorkflowVersionRepository versionRepository;
    private final ConditionCompiler compiler;
    private final JsonSerializer jsonSerializer;

    @PostConstruct
    public void warm() {
        try {
            versionRepository.findByStatus(VersionStatus.PUBLISHED)
                .forEach(v -> cache.put(v.getId(), compile(v.getId(), v.getGraphJson())));
        } catch (Exception e) {
            log.warn("Workflow cache warm-up skipped - schema may not be initialized yet: {}", e.getMessage());
        }
    }

    @Override
    public CompiledWorkflow get(Long workflowVersionId) {
        return cache.computeIfAbsent(workflowVersionId, id -> {
            var version = versionRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException(id));
            return compile(id, version.getGraphJson());
        });
    }

    @Override
    public void evict(Long workflowVersionId) {
        cache.remove(workflowVersionId);
    }

    public void onPublish(Long workflowVersionId) {
        evict(workflowVersionId);
        get(workflowVersionId);
    }

    private CompiledWorkflow compile(Long versionId, String graphJson) {
        WorkflowGraph graph = jsonSerializer.fromJson(graphJson, WorkflowGraph.class);
        CompiledWorkflow compiled = compiler.compile(graph);
        return new CompiledWorkflow(versionId, compiled.startNodeId(), compiled.nodes(), compiled.outgoingTransitions());
    }
}
