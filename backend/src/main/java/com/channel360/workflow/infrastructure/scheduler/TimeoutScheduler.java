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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeoutScheduler {

    private final WorkflowTaskRepository taskRepository;
    private final WorkflowEngine workflowEngine;
    private final WorkflowDefinitionProvider definitionProvider;
    private final ExecutionApplier executionApplier;

    @Scheduled(fixedRate = 30000)
    public void timeoutOverdueTasks() {
        List<WorkflowTask> overdueTasks = taskRepository.findByStatus(TaskStatus.PENDING);
        for (WorkflowTask task : overdueTasks) {
            try {
                CompiledWorkflow compiled = definitionProvider.get(
                    task.getRequest().getWorkflowVersion().getId());
                NodeRef currentNode = compiled.getNode(task.getNode().getNodeUuid()).orElse(null);
                if (currentNode == null) continue;

                ExecutionResult result = workflowEngine.execute(
                    compiled, currentNode, TransitionAction.TIMEOUT,
                    new BusinessContext(Map.of("taskId", task.getId())));

                executionApplier.apply(task.getRequest(), currentNode, result);
                log.info("Timed out task {} in workflow request {}", task.getId(), task.getRequest().getId());
            } catch (Exception e) {
                log.error("Failed to timeout task {}", task.getId(), e);
            }
        }
    }
}
