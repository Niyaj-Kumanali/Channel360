package com.channel360.regionapprover.api;

import com.channel360.regionapprover.domain.RegionApprover;
import com.channel360.regionapprover.infrastructure.RegionApproverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionApproverFacade {

    private final RegionApproverRepository regionApproverRepository;

    public Long findApproverUserId(Long regionId, Long roleId, Long userId) {
        return regionApproverRepository
                .findByRegionIdAndRoleIdAndUserIdAndActiveFlagTrue(regionId, roleId, userId)
                .map(RegionApprover::getUserId)
                .orElse(null);
    }

    public boolean existsByRegionId(Long regionId) {
        return regionApproverRepository.existsByRegionIdAndActiveFlagTrue(regionId);
    }
}
