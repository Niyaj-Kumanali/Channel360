package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.ApproverRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApproverRuleRepository extends JpaRepository<ApproverRule, Long> {
    List<ApproverRule> findByAssignmentId(Long assignmentId);
    void deleteByAssignmentId(Long assignmentId);
}
