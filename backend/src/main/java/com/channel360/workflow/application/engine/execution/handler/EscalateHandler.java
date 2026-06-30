package com.channel360.workflow.application.engine.execution.handler;

import com.channel360.workflow.application.engine.execution.TransitionService;
import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.model.*;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EscalateHandler implements WorkflowActionHandler {

    private final TransitionService transitionService;

    @Override
    public TransitionAction action() { return TransitionAction.ESCALATE; }

    @Override
    public ExecutionResult handle(CompiledWorkflow workflow, NodeRef currentNode, BusinessContext ctx) {
        CompiledTransition transition = transitionService.resolveTransition(workflow, currentNode, TransitionAction.ESCALATE, ctx);
        NodeRef targetNode = workflow.getNode(transition.targetNodeId()).orElseThrow();
        AuditPlan audit = new AuditPlan(currentNode.nodeId(), targetNode.nodeId(), TransitionAction.ESCALATE, null, "Escalated");
        return transitionService.advanceToNode(workflow, targetNode, ctx, audit);
    }
}
