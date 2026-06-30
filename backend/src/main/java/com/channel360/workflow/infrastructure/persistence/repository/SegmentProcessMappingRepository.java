package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.SegmentProcessMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SegmentProcessMappingRepository extends JpaRepository<SegmentProcessMapping, Long> {
    List<SegmentProcessMapping> findBySegmentId(Long segmentId);
    List<SegmentProcessMapping> findByBusinessProcessId(Long businessProcessId);
    void deleteBySegmentId(Long segmentId);
}
