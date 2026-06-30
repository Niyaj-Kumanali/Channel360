package com.channel360.workflow.application.engine.execution;

import com.channel360.workflow.application.engine.execution.handler.WorkflowActionHandler;
import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.exception.NoValidTransitionException;
import com.channel360.workflow.domain.model.CompiledWorkflow;
import com.channel360.workflow.domain.model.ExecutionResult;
import com.channel360.workflow.domain.model.NodeRef;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Pure domain engine. Invariants:
 * <ul>
 *   <li>Never calls repositories</li>
 *   <li>Never publishes events</li>
 *   <li>Never writes to the database</li>
 *   <li>Never opens transactions</li>
 *   <li>Never knows Spring Security</li>
 *   <li>Never knows HTTP or servlets</li>
 *   <li>Never knows JPA or entity classes</li>
 *   <li>Always returns immutable ExecutionResult</li>
 *   <li>Always stateless — no mutable fields</li>
 * </ul>
 */
@Component
public class WorkflowEngine {

    private final Map<TransitionAction, WorkflowActionHandler> handlers;
    private final TransitionService transitionService;

    public WorkflowEngine(List<WorkflowActionHandler> handlerList, TransitionService transitionService) {
        this.handlers = handlerList.stream()
            .collect(Collectors.toMap(WorkflowActionHandler::action, Function.identity()));
        this.transitionService = transitionService;
    }

    public ExecutionResult execute(CompiledWorkflow workflow, NodeRef currentNode,
                                    TransitionAction action, BusinessContext ctx) {
        WorkflowActionHandler handler = handlers.get(action);
        if (handler == null) {
            throw new NoValidTransitionException(currentNode.nodeId(), action.name());
        }
        return handler.handle(workflow, currentNode, ctx);
    }

    public ExecutionResult startWorkflow(CompiledWorkflow workflow, BusinessContext ctx) {
        NodeRef startNode = workflow.getNode(workflow.startNodeId())
            .orElseThrow(() -> new IllegalStateException("Start node not found"));
        WorkflowActionHandler handler = handlers.get(TransitionAction.SUBMIT);
        if (handler == null) {
            throw new IllegalStateException("No handler registered for SUBMIT action");
        }
        return handler.handle(workflow, startNode, ctx);
    }

    public ExecutionResult executeAction(CompiledWorkflow workflow, Long currentNodeDbId,
                                          UUID currentNodeUuid, TransitionAction action,
                                          BusinessContext ctx) {
        NodeRef currentNode = workflow.getNode(currentNodeUuid)
            .orElseThrow(() -> new NoValidTransitionException(currentNodeUuid, action.name()));
        return execute(workflow, currentNode, action, ctx);
    }
}
