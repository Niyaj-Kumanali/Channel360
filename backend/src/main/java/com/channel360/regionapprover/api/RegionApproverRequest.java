package com.channel360.regionapprover.api;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RegionApproverRequest(
    Long id,
    @NotNull(message = "Region ID is required") Long regionId,
    @NotNull(message = "Role ID is required") Long roleId,
    @NotNull(message = "User ID is required") Long userId
) {}
