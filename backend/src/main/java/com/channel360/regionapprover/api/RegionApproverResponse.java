package com.channel360.regionapprover.api;

import lombok.Builder;

@Builder
public record RegionApproverResponse(
    Long id,
    Long regionId,
    String regionName,
    String regionPath,
    Long roleId,
    String roleName,
    Long userId,
    String userName,
    String userEmail,
    Boolean activeFlag,
    String createdBy,
    String updatedBy
) {}