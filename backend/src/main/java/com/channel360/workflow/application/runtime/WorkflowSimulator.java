package com.channel360.workflow.application.runtime;

import com.channel360.workflow.application.engine.execution.WorkflowEngine;
import com.channel360.workflow.application.engine.provider.WorkflowDefinitionProvider;
import com.channel360.workflow.domain.model.*;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkflowSimulator {

    private final WorkflowEngine workflowEngine;
    private final WorkflowDefinitionProvider definitionProvider;

    public SimulateResult simulate(Long workflowVersionId, BusinessContext ctx) {
        CompiledWorkflow compiled = definitionProvider.get(workflowVersionId);
        List<ExecutionTraceStep> trace = new ArrayList<>();
        NodeRef currentNode = compiled.getNode(compiled.startNodeId())
            .orElseThrow(() -> new IllegalStateException("Start node not found"));

        ExecutionResult result = null;
        int maxSteps = 100;
        for (int i = 0; i < maxSteps; i++) {
            var action = currentNode.type() == com.channel360.workflow.domain.enums.NodeType.START
                ? com.channel360.workflow.domain.enums.TransitionAction.SUBMIT
                : com.channel360.workflow.domain.enums.TransitionAction.APPROVE;

            result = workflowEngine.execute(compiled, currentNode, action, ctx);

            trace.add(new ExecutionTraceStep(
                currentNode, null, "MATCHED", List.of(), currentNode, Instant.now()));

            if (result.terminal()) break;

            if (result.nextTask().isPresent()) {
                break;
            }

            if (result.audit().isPresent()) {
                var audit = result.audit().get();
                UUID nextId = audit.toNodeId();
                currentNode = compiled.getNode(nextId)
                    .orElseThrow(() -> new IllegalStateException("Node not found: " + nextId));
            } else {
                break;
            }
        }

        return new SimulateResult(
            result != null ? result : ExecutionResult.approved("Simulation completed"),
            trace
        );
    }

}
