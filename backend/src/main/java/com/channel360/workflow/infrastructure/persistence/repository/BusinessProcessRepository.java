package com.channel360.workflow.infrastructure.persistence.repository;

import com.channel360.workflow.domain.entity.BusinessProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BusinessProcessRepository extends JpaRepository<BusinessProcess, Long> {
    Optional<BusinessProcess> findByCode(String code);
    boolean existsByName(String name);
}
