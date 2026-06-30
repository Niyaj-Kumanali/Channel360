package com.channel360.workflow.application.engine.provider;

import com.channel360.workflow.domain.model.CompiledWorkflow;

public interface WorkflowDefinitionProvider {
    CompiledWorkflow get(Long workflowVersionId);
    void evict(Long workflowVersionId);
}
