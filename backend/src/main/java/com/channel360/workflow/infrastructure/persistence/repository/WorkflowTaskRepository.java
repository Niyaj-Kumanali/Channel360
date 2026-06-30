package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.WorkflowTask;
import com.channel360.workflow.domain.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, Long> {
    List<WorkflowTask> findByRequestId(Long requestId);
    List<WorkflowTask> findByAssignedUserIdAndStatus(Long assignedUserId, TaskStatus status);
    List<WorkflowTask> findByStatus(TaskStatus status);
    long countByRequestIdAndStatus(Long requestId, TaskStatus status);
}
