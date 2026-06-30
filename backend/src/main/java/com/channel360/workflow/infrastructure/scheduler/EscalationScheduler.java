package com.channel360.workflow.infrastructure.scheduler;

import com.channel360.workflow.application.engine.execution.WorkflowEngine;
import com.channel360.workflow.application.engine.provider.WorkflowDefinitionProvider;
import com.channel360.workflow.application.runtime.ExecutionApplier;
import com.channel360.workflow.domain.entity.WorkflowTask;
import com.channel360.workflow.domain.enums.TaskStatus;
import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.model.CompiledWorkflow;
import com.channel360.workflow.domain.model.ExecutionResult;
import com.channel360.workflow.domain.model.NodeRef;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EscalationScheduler {

    private final WorkflowTaskRepository taskRepository;
    private final WorkflowEngine workflowEngine;
    private final WorkflowDefinitionProvider definitionProvider;
    private final ExecutionApplier executionApplier;

    @Scheduled(fixedRate = 60000)
    public void escalateOverdueTasks() {
        List<WorkflowTask> overdue = taskRepository.findByStatus(TaskStatus.PENDING);
        for (WorkflowTask task : overdue) {
            try {
                CompiledWorkflow compiled = definitionProvider.get(
                    task.getRequest().getWorkflowVersion().getId());
                NodeRef currentNode = compiled.getNode(task.getNode().getNodeUuid()).orElse(null);
                if (currentNode == null) continue;

                ExecutionResult result = workflowEngine.execute(
                    compiled, currentNode, TransitionAction.ESCALATE,
                    new BusinessContext(Map.of("taskId", task.getId())));

                executionApplier.apply(task.getRequest(), currentNode, result);
            } catch (Exception e) {
                log.error("Failed to escalate task {}", task.getId(), e);
            }
        }
    }
}
