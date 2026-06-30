package com.channel360.workflow.domain.entity;

import com.channel360.workflow.domain.enums.TransitionAction;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workflow_transitions")
public class WorkflowTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transition_uuid", nullable = false, unique = true)
    private java.util.UUID transitionUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_node_id", nullable = false)
    private WorkflowNode sourceNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_node_id", nullable = false)
    private WorkflowNode targetNode;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private TransitionAction action;

    @Column(length = 255)
    private String label;

    @Builder.Default
    @Column(nullable = false)
    private Integer priority = 0;

    @Builder.Default
    @Column(name = "entity_version")
    @Version
    private Long entityVersion = 0L;

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
