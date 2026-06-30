package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.WorkflowRequest;
import com.channel360.workflow.domain.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WorkflowRequestRepository extends JpaRepository<WorkflowRequest, Long> {
    List<WorkflowRequest> findByRequestorIdOrderByCreatedAtDesc(Long requestorId);
    List<WorkflowRequest> findByStatus(RequestStatus status);
    Optional<WorkflowRequest> findByIdempotencyKey(String idempotencyKey);
    boolean existsByWorkflowVersionIdAndStatus(Long workflowVersionId, RequestStatus status);
}
