package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SegmentRepository extends JpaRepository<Segment, Long> {
    Optional<Segment> findByCode(String code);
    boolean existsByName(String name);
}
