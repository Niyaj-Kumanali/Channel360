package com.channel360.region.api;

import java.util.List;

public interface RegionFacade {
    RegionResponse getById(Long id);
    String getRegionNameById(Long id);
    String getRegionPathById(Long id);
    List<RegionResponse> getAncestors(Long regionId);
}
