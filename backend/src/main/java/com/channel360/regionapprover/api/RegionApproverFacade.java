package com.channel360.regionapprover.api;

public interface RegionApproverFacade {
    Long findApproverUserId(Long regionId, Long roleId, Long userId);
    boolean existsByRegionId(Long regionId);
}
