package com.channel360.workflow.application.runtime;

import com.channel360.workflow.domain.entity.WorkflowHistory;
import com.channel360.workflow.domain.entity.WorkflowRequest;
import com.channel360.workflow.domain.entity.WorkflowTask;
import com.channel360.workflow.domain.enums.RequestStatus;
import com.channel360.workflow.domain.enums.TaskStatus;
import com.channel360.workflow.domain.model.AuditPlan;
import com.channel360.workflow.domain.model.ExecutionResult;
import com.channel360.workflow.domain.model.NodeRef;
import com.channel360.workflow.domain.model.TaskPlan;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowHistoryRepository;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowNodeRepository;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExecutionApplier {

    private final WorkflowTaskRepository taskRepository;
    private final WorkflowHistoryRepository historyRepository;
    private final WorkflowNodeRepository nodeRepository;
    private final RequestService requestService;
    private final TaskService taskService;

    @Transactional
    public void apply(WorkflowRequest request, NodeRef fromNode, ExecutionResult result) {
        result.audit().ifPresent(audit -> saveHistory(request, audit));

        result.newStatus().ifPresent(status -> requestService.updateStatus(request, status));

        result.nextTask().ifPresent(taskPlan -> {
            WorkflowTask task = taskService.createTask(request, taskPlan);
            if (task != null) {
                updateRequestNode(request, taskPlan);
            }
        });
    }

    private void saveHistory(WorkflowRequest request, AuditPlan audit) {
        WorkflowHistory history = WorkflowHistory.builder()
            .request(request).action(audit.action())
            .actorId(audit.actorId() != null ? Long.parseLong(audit.actorId()) : null)
            .comments(audit.comment()).build();
        historyRepository.save(history);
    }

    private void updateRequestNode(WorkflowRequest request, TaskPlan taskPlan) {
        nodeRepository.findByNodeUuid(taskPlan.nodeId()).ifPresent(node -> {
            request.setCurrentNode(node);
        });
    }
}
