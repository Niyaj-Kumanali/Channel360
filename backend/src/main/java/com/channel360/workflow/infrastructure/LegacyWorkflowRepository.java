package com.channel360.workflow.infrastructure;

import com.channel360.workflow.domain.ApprovalWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LegacyWorkflowRepository extends JpaRepository<ApprovalWorkflow, Long> {

    @Procedure("sp_workflow_save")
    void spSave(
        @Param("p_id") Long id,
        @Param("p_name") String name,
        @Param("p_description") String description,
        @Param("p_module") String module,
        @Param("p_active") Boolean active,
        @Param("p_user") String user
    );

    @Procedure("sp_workflow_delete")
    void spDelete(@Param("p_id") Long id);

    List<ApprovalWorkflow> findByDeletedFlagFalseOrderByModuleAscNameAsc();

    @Query("SELECT w FROM ApprovalWorkflow w WHERE w.deletedFlag = false AND w.id = :id")
    Optional<ApprovalWorkflow> findActiveById(@Param("id") Long id);

    @Query("SELECT w FROM ApprovalWorkflow w WHERE w.deletedFlag = false AND w.name = :name")
    Optional<ApprovalWorkflow> findActiveByName(@Param("name") String name);
}
