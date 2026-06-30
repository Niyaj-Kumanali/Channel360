package com.channel360.workflow.application.runtime;

import com.channel360.workflow.application.engine.execution.WorkflowEngine;
import com.channel360.workflow.application.engine.provider.WorkflowDefinitionProvider;
import com.channel360.workflow.domain.entity.WorkflowRequest;
import com.channel360.workflow.domain.entity.WorkflowTask;
import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.exception.WorkflowNotFoundException;
import com.channel360.workflow.domain.model.CompiledWorkflow;
import com.channel360.workflow.domain.model.ExecutionResult;
import com.channel360.workflow.domain.model.NodeRef;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NormalExecutionService {

    private final WorkflowEngine workflowEngine;
    private final WorkflowDefinitionProvider definitionProvider;
    private final RequestService requestService;
    private final TaskService taskService;
    private final ExecutionApplier executionApplier;
    private final IdempotencyService idempotencyService;

    @Transactional
    public ExecutionResult startWorkflow(Long workflowId, BusinessContext ctx, String idempotencyKey) {
        if (idempotencyService.isProcessed(idempotencyKey)) {
            return ExecutionResult.approved("Already processed");
        }

        CompiledWorkflow compiled = definitionProvider.get(workflowId);
        ExecutionResult result = workflowEngine.startWorkflow(compiled, ctx);

        WorkflowRequest request = requestService.createRequest(workflowId,
            ctx.getRaw("requestType") != null ? ctx.getRaw("requestType").toString() : "GENERIC",
            ctx.getRaw("requestorId") != null ? Long.parseLong(ctx.getRaw("requestorId").toString()) : null,
            ctx, idempotencyKey);

        NodeRef startNode = compiled.getNode(compiled.startNodeId())
            .orElseThrow(() -> new WorkflowNotFoundException("Start node not found"));
        executionApplier.apply(request, startNode, result);
        idempotencyService.markProcessed(idempotencyKey);
        return result;
    }

    @Transactional
    public ExecutionResult executeAction(Long taskId, TransitionAction action, Long userId,
                                          BusinessContext ctx, String idempotencyKey) {
        if (idempotencyService.isProcessed(idempotencyKey)) {
            return ExecutionResult.approved("Already processed");
        }

        WorkflowTask task = taskService.getTask(taskId);
        WorkflowRequest request = task.getRequest();
        CompiledWorkflow compiled = definitionProvider.get(request.getWorkflowVersion().getId());

        UUID currentNodeUuid = request.getCurrentNode() != null
            ? request.getCurrentNode().getNodeUuid()
            : compiled.startNodeId();

        NodeRef currentNode = compiled.getNode(currentNodeUuid)
            .orElseThrow(() -> new WorkflowNotFoundException("Current node not found in compiled workflow"));

        taskService.markActedOn(task, userId, switch (action) {
            case APPROVE -> com.channel360.workflow.domain.enums.TaskStatus.APPROVED;
            case REJECT -> com.channel360.workflow.domain.enums.TaskStatus.REJECTED;
            case SEND_BACK -> com.channel360.workflow.domain.enums.TaskStatus.PENDING;
            case DELEGATE -> com.channel360.workflow.domain.enums.TaskStatus.DELEGATED;
            default -> com.channel360.workflow.domain.enums.TaskStatus.APPROVED;
        }, ctx != null ? ctx.getRaw("comment") != null ? ctx.getRaw("comment").toString() : null : null);

        ExecutionResult result = workflowEngine.execute(compiled, currentNode, action, ctx);
        executionApplier.apply(request, currentNode, result);
        idempotencyService.markProcessed(idempotencyKey);
        return result;
    }
}
