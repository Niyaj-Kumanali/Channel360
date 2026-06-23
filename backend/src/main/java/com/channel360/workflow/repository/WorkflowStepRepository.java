package com.channel360.workflow.repository;

import com.channel360.workflow.entity.ApprovalWorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowStepRepository extends JpaRepository<ApprovalWorkflowStep, Long> {

    @Procedure("sp_workflow_step_save")
    void spSave(
        @Param("p_id") Long id,
        @Param("p_workflow_id") Long workflowId,
        @Param("p_step_order") Integer stepOrder,
        @Param("p_role_name") String roleName,
        @Param("p_label") String label,
        @Param("p_mandatory") Boolean mandatory,
        @Param("p_sla_hours") Integer slaHours,
        @Param("p_escalation_role") String escalationRole,
        @Param("p_description") String description,
        @Param("p_user") String user
    );

    @Procedure("sp_workflow_step_delete")
    void spDelete(@Param("p_id") Long id);

    List<ApprovalWorkflowStep> findByWorkflowIdAndDeletedFlagFalseOrderByStepOrder(Long workflowId);

    @Query("SELECT s FROM ApprovalWorkflowStep s WHERE s.deletedFlag = false AND s.workflowId = :workflowId AND s.stepOrder = :stepOrder AND s.label = :label AND s.roleName = :roleName")
    Optional<ApprovalWorkflowStep> findActiveByWorkflowIdAndStepOrderAndLabelAndRoleName(
            @Param("workflowId") Long workflowId,
            @Param("stepOrder") Integer stepOrder,
            @Param("label") String label,
            @Param("roleName") String roleName);

    @Query("SELECT s FROM ApprovalWorkflowStep s WHERE s.deletedFlag = false AND s.id = :id")
    Optional<ApprovalWorkflowStep> findActiveById(@Param("id") Long id);
}
