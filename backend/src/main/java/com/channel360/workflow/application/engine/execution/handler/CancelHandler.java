package com.channel360.workflow.application.engine.execution.handler;

import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.model.*;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import org.springframework.stereotype.Component;

@Component
public class CancelHandler implements WorkflowActionHandler {
    @Override
    public TransitionAction action() { return TransitionAction.CANCEL; }

    @Override
    public ExecutionResult handle(CompiledWorkflow workflow, NodeRef currentNode, BusinessContext ctx) {
        return ExecutionResult.cancelled("Workflow cancelled at node: " + currentNode.name());
    }
}
