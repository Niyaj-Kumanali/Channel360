package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    List<Workflow> findByActiveTrue();
}
