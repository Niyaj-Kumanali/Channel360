package com.channel360.workflow.application.configuration;

import com.channel360.workflow.domain.entity.BusinessProcess;
import com.channel360.workflow.domain.exception.WorkflowNotFoundException;
import com.channel360.workflow.infrastructure.persistence.repository.BusinessProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessProcessService {

    private final BusinessProcessRepository businessProcessRepository;

    @Transactional
    public BusinessProcess create(String name, String code, String description) {
        return businessProcessRepository.save(BusinessProcess.builder()
            .name(name).code(code).description(description).build());
    }

    public BusinessProcess getById(Long id) {
        return businessProcessRepository.findById(id)
            .orElseThrow(() -> new WorkflowNotFoundException("BusinessProcess not found: " + id));
    }

    public List<BusinessProcess> getAll() {
        return businessProcessRepository.findAll();
    }

    @Transactional
    public BusinessProcess update(Long id, String name, String description) {
        BusinessProcess bp = getById(id);
        bp.setName(name);
        bp.setDescription(description);
        return businessProcessRepository.save(bp);
    }

    @Transactional
    public void delete(Long id) {
        businessProcessRepository.deleteById(id);
    }
}
