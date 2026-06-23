package com.channel360.approval.repository;

import com.channel360.approval.entity.ApprovalTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalTaskRepository extends JpaRepository<ApprovalTask, Long> {

    List<ApprovalTask> findByApprovalRequestIdOrderByCreatedAtAsc(Long approvalRequestId);

    List<ApprovalTask> findByAssignedUserIdAndStatusOrderByCreatedAtDesc(Long assignedUserId, String status);

    @Query("SELECT t FROM ApprovalTask t WHERE t.id = :id")
    Optional<ApprovalTask> findActiveById(@Param("id") Long id);
}
