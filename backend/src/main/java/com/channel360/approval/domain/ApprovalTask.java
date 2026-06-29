package com.channel360.approval.domain;

import com.channel360.common.domain.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "approval_tasks")
public class ApprovalTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "approval_request_id", nullable = false)
    private Long approvalRequestId;

    @Column(name = "workflow_step_id", nullable = false)
    private Long workflowStepId;

    @Column(name = "assigned_role_id", nullable = false)
    private Long assignedRoleId;

    @Column(name = "assigned_user_id")
    private Long assignedUserId;

    @Column(name = "assigned_region_id")
    private Long assignedRegionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApprovalStatus status;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_by")
    private Long rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
