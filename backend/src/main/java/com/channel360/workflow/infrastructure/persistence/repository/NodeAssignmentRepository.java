package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.NodeAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface NodeAssignmentRepository extends JpaRepository<NodeAssignment, Long> {
    Optional<NodeAssignment> findByNodeId(Long nodeId);
    Optional<NodeAssignment> findByAssignmentUuid(UUID assignmentUuid);
    void deleteByNodeId(Long nodeId);
}
