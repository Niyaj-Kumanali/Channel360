package com.channel360.approval.infrastructure;

import com.channel360.approval.domain.ApprovalRequest;
import com.channel360.common.domain.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    List<ApprovalRequest> findByRequestorIdOrderByCreatedAtDesc(Long requestorId);

    List<ApprovalRequest> findByStatusOrderByCreatedAtDesc(ApprovalStatus status);

    @Query("SELECT r FROM ApprovalRequest r WHERE r.id = :id")
    Optional<ApprovalRequest> findActiveById(@Param("id") Long id);
}
