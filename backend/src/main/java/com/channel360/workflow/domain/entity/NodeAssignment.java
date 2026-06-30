package com.channel360.workflow.domain.entity;

import com.channel360.workflow.domain.enums.AssignmentPolicy;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "node_assignments")
public class NodeAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_uuid", nullable = false, unique = true)
    private java.util.UUID assignmentUuid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    private WorkflowNode node;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy", nullable = false, length = 30)
    private AssignmentPolicy policy;

    @Column(name = "required_approval_count")
    private Integer requiredApprovalCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
