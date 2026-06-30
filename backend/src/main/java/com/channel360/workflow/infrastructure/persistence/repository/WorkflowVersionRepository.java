package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.WorkflowVersion;
import com.channel360.workflow.domain.enums.VersionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WorkflowVersionRepository extends JpaRepository<WorkflowVersion, Long> {
    List<WorkflowVersion> findByWorkflowIdOrderByVersionNumberDesc(Long workflowId);
    Optional<WorkflowVersion> findTopByWorkflowIdAndStatusOrderByVersionNumberDesc(Long workflowId, VersionStatus status);
    List<WorkflowVersion> findByStatus(VersionStatus status);
    boolean existsByWorkflowIdAndStatus(Long workflowId, VersionStatus status);
}
