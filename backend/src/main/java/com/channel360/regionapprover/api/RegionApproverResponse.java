package com.channel360.regionapprover.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionApproverResponse {

    private Long id;
    private Long regionId;
    private String regionName;
    private String regionPath;
    private Long roleId;
    private String roleName;
    private Long userId;
    private String userName;
    private String userEmail;
    private Boolean activeFlag;
    private String createdBy;
    private String updatedBy;
}
