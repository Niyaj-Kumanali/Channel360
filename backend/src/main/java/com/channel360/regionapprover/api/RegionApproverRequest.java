package com.channel360.regionapprover.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionApproverRequest {

    private Long id;

    @NotNull(message = "Region ID is required")
    private Long regionId;

    @NotNull(message = "Role ID is required")
    private Long roleId;

    @NotNull(message = "User ID is required")
    private Long userId;
}
