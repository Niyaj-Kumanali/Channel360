package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.WorkflowNode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowNodeRepository extends JpaRepository<WorkflowNode, Long> {
    List<WorkflowNode> findByWorkflowVersionId(Long workflowVersionId);
    Optional<WorkflowNode> findByNodeUuid(UUID nodeUuid);
    List<WorkflowNode> findByNodeUuidIn(List<UUID> nodeUuids);
    void deleteByWorkflowVersionId(Long workflowVersionId);
}
