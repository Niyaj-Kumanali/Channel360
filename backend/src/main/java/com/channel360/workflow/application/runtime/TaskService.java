package com.channel360.workflow.application.runtime;

import com.channel360.workflow.domain.entity.WorkflowTask;
import com.channel360.workflow.domain.enums.TaskStatus;
import com.channel360.workflow.domain.exception.TaskAlreadyActedOnException;
import com.channel360.workflow.domain.exception.WorkflowNotFoundException;
import com.channel360.workflow.domain.model.ResolvedAssignment;
import com.channel360.workflow.domain.model.TaskPlan;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final WorkflowTaskRepository taskRepository;

    @Transactional
    public WorkflowTask createTask(com.channel360.workflow.domain.entity.WorkflowRequest request,
                                    TaskPlan plan) {
        if (plan.assignees().isEmpty()) return null;
        ResolvedAssignment first = plan.assignees().get(0);
        WorkflowTask task = WorkflowTask.builder()
            .request(request)
            .assignedUserId(first.userId())
            .status(TaskStatus.PENDING)
            .build();
        return taskRepository.save(task);
    }

    public WorkflowTask getTask(Long taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow(() -> new WorkflowNotFoundException("Task not found: " + taskId));
    }

    public List<WorkflowTask> getTasksForUser(Long userId) {
        return taskRepository.findByAssignedUserIdAndStatus(userId, TaskStatus.PENDING);
    }

    public List<WorkflowTask> getTasksForRequest(Long requestId) {
        return taskRepository.findByRequestId(requestId);
    }

    @Transactional
    public void markActedOn(WorkflowTask task, Long userId, TaskStatus newStatus, String comments) {
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new TaskAlreadyActedOnException(task.getId());
        }
        task.setStatus(newStatus);
        task.setActedBy(userId);
        task.setActedAt(LocalDateTime.now());
        task.setComments(comments);
        taskRepository.save(task);
    }

    public long countCompletedTasks(Long requestId) {
        return taskRepository.countByRequestIdAndStatus(requestId, TaskStatus.APPROVED);
    }
}
