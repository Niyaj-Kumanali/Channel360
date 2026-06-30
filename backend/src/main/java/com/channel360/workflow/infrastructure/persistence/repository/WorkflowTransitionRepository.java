package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, Long> {
    List<WorkflowTransition> findBySourceNodeWorkflowVersionId(Long workflowVersionId);
    List<WorkflowTransition> findBySourceNodeId(Long sourceNodeId);
    Optional<WorkflowTransition> findByTransitionUuid(UUID transitionUuid);
    void deleteBySourceNodeWorkflowVersionId(Long workflowVersionId);
}
