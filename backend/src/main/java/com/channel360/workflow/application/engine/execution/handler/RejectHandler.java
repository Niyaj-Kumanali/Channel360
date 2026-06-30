package com.channel360.workflow.application.engine.execution.handler;

import com.channel360.workflow.application.engine.execution.TransitionService;
import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.model.*;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RejectHandler implements WorkflowActionHandler {

    private final TransitionService transitionService;

    @Override
    public TransitionAction action() { return TransitionAction.REJECT; }

    @Override
    public ExecutionResult handle(CompiledWorkflow workflow, NodeRef currentNode, BusinessContext ctx) {
        CompiledTransition transition = transitionService.resolveTransition(workflow, currentNode, TransitionAction.REJECT, ctx);
        NodeRef targetNode = workflow.getNode(transition.targetNodeId()).orElseThrow();
        AuditPlan audit = new AuditPlan(currentNode.nodeId(), targetNode.nodeId(), TransitionAction.REJECT, null, "Rejected");
        if (targetNode.terminalType() != null) {
            return ExecutionResult.rejected("Workflow rejected at node: " + currentNode.name());
        }
        return transitionService.advanceToNode(workflow, targetNode, ctx, audit);
    }
}
