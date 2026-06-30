package com.channel360.workflow.domain.entity;

import com.channel360.workflow.domain.enums.ApproverType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "approver_rules")
public class ApproverRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_uuid", nullable = false, unique = true)
    private java.util.UUID ruleUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private NodeAssignment assignment;

    @Enumerated(EnumType.STRING)
    @Column(name = "approver_type", nullable = false, length = 30)
    private ApproverType approverType;

    @Column(name = "role_name", length = 100)
    private String roleName;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "region_id")
    private Long regionId;

    @Column(length = 100)
    private String department;

    @Column(name = "dynamic_provider", length = 200)
    private String dynamicProvider;

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
