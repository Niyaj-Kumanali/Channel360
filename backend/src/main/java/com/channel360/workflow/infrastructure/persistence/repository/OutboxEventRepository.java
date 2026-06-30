package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.OutboxEvent;
import com.channel360.workflow.domain.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Query(value = """
        SELECT * FROM workflow_outbox
        WHERE status = 'PENDING'
          AND (next_retry_at IS NULL OR next_retry_at <= :now)
          AND retry_count < :maxRetries
        ORDER BY created_at
        FOR UPDATE SKIP LOCKED
        LIMIT :limit
        """, nativeQuery = true)
    List<OutboxEvent> findPendingEvents(
        @Param("now") LocalDateTime now,
        @Param("maxRetries") int maxRetries,
        @Param("limit") int limit
    );
}
