package com.channel360.workflow.application.configuration;

import com.channel360.workflow.domain.entity.Segment;
import com.channel360.workflow.domain.exception.WorkflowNotFoundException;
import com.channel360.workflow.infrastructure.persistence.repository.SegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SegmentService {

    private final SegmentRepository segmentRepository;

    @Transactional
    public Segment create(String name, String code, String description) {
        return segmentRepository.save(Segment.builder()
            .name(name).code(code).description(description).build());
    }

    public Segment getById(Long id) {
        return segmentRepository.findById(id)
            .orElseThrow(() -> new WorkflowNotFoundException("Segment not found: " + id));
    }

    public List<Segment> getAll() {
        return segmentRepository.findAll();
    }

    @Transactional
    public Segment update(Long id, String name, String description) {
        Segment segment = getById(id);
        segment.setName(name);
        segment.setDescription(description);
        return segmentRepository.save(segment);
    }

    @Transactional
    public void delete(Long id) {
        segmentRepository.deleteById(id);
    }
}
