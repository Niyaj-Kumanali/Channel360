package com.channel360.workflow.application.engine.execution;

import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.model.CompiledWorkflow;
import com.channel360.workflow.domain.model.NodeRef;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import java.util.HashMap;
import java.util.Map;

public class WorkflowExecutionContext {

    private final CompiledWorkflow workflow;
    private final BusinessContext businessContext;
    private final Map<String, Object> localCache = new HashMap<>();

    public WorkflowExecutionContext(CompiledWorkflow workflow, BusinessContext businessContext) {
        this.workflow = workflow;
        this.businessContext = businessContext;
    }

    public CompiledWorkflow workflow() { return workflow; }
    public BusinessContext businessContext() { return businessContext; }

    @SuppressWarnings("unchecked")
    public <T> T getCache(String key) {
        return (T) localCache.get(key);
    }

    public void putCache(String key, Object value) {
        localCache.put(key, value);
    }

    public NodeRef findStartNode() {
        return workflow.getNode(workflow.startNodeId())
            .orElseThrow(() -> new IllegalStateException("Start node not found in compiled workflow"));
    }
}
