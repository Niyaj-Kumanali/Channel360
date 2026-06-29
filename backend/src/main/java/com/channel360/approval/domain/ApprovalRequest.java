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
@Table(name = "approval_requests")
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workflow_id", nullable = false)
    private Long workflowId;

    @Column(name = "request_type", nullable = false, length = 100)
    private String requestType;

    @Column(name = "request_reference_id")
    private Long requestReferenceId;

    @Column(name = "request_region_id")
    private Long requestRegionId;

    @Column(name = "requestor_id", nullable = false)
    private Long requestorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApprovalStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
