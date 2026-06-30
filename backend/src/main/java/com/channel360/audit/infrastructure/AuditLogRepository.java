package com.channel360.audit.infrastructure;

import com.channel360.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Procedure("sp_audit_log_insert")
    Long spInsert(
        @Param("p_user_id") Long userId,
        @Param("p_action") String action,
        @Param("p_module_name") String moduleName,
        @Param("p_entity_name") String entityName,
        @Param("p_entity_id") Long entityId,
        @Param("p_old_data") String oldData,
        @Param("p_new_data") String newData
    );

    @Query("SELECT a FROM AuditLog a ORDER BY a.createdAt DESC")
    List<AuditLog> findAllOrderByCreatedAtDesc();

    @Query("SELECT a FROM AuditLog a WHERE a.moduleName = :moduleName ORDER BY a.createdAt DESC")
    List<AuditLog> findByModule(@Param("moduleName") String moduleName);

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId ORDER BY a.createdAt DESC")
    List<AuditLog> findByUserId(@Param("userId") Long userId);
}
