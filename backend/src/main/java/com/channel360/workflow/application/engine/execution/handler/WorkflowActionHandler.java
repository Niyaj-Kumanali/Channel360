package com.channel360.workflow.application.engine.execution.handler;

import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.model.CompiledWorkflow;
import com.channel360.workflow.domain.model.ExecutionResult;
import com.channel360.workflow.domain.model.NodeRef;
import com.channel360.workflow.domain.valueobject.BusinessContext;

public interface WorkflowActionHandler {
    TransitionAction action();
    ExecutionResult handle(CompiledWorkflow workflow, NodeRef currentNode, BusinessContext ctx);
}
