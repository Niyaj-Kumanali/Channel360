package com.channel360.workflow.application.engine.execution.handler;

import com.channel360.workflow.application.engine.execution.TransitionService;
import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.model.*;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendBackHandler implements WorkflowActionHandler {

    private final TransitionService transitionService;

    @Override
    public TransitionAction action() { return TransitionAction.SEND_BACK; }

    @Override
    public ExecutionResult handle(CompiledWorkflow workflow, NodeRef currentNode, BusinessContext ctx) {
        CompiledTransition transition = transitionService.resolveTransition(workflow, currentNode, TransitionAction.SEND_BACK, ctx);
        NodeRef targetNode = workflow.getNode(transition.targetNodeId()).orElseThrow();
        AuditPlan audit = new AuditPlan(currentNode.nodeId(), targetNode.nodeId(), TransitionAction.SEND_BACK, null, "Sent back");
        return transitionService.advanceToNode(workflow, targetNode, ctx, audit);
    }
}
