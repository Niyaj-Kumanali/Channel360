package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.WorkflowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkflowHistoryRepository extends JpaRepository<WorkflowHistory, Long> {
    List<WorkflowHistory> findByRequestIdOrderByCreatedAtAsc(Long requestId);
}
