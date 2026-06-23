package com.channel360.region.api;

import com.channel360.region.domain.Region;
import com.channel360.region.infrastructure.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RegionFacadeImpl implements RegionFacade {

    private final RegionRepository regionRepository;

    @Override
    public RegionResponse getById(Long id) {
        Region region = regionRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Region not found with id: " + id));
        return toResponse(region);
    }

    @Override
    public String getRegionNameById(Long id) {
        return regionRepository.findActiveById(id)
                .map(Region::getName)
                .orElse(null);
    }

    @Override
    public String getRegionPathById(Long id) {
        return regionRepository.findActiveById(id)
                .map(Region::getPath)
                .orElse(null);
    }

    @Override
    public List<RegionResponse> getAncestors(Long regionId) {
        List<RegionResponse> ancestors = new ArrayList<>();
        Region current = regionRepository.findActiveById(regionId).orElse(null);
        while (current != null && current.getParentId() != null) {
            Region parent = regionRepository.findActiveById(current.getParentId()).orElse(null);
            if (parent != null) {
                ancestors.add(toResponse(parent));
                current = parent;
            } else {
                break;
            }
        }
        return ancestors;
    }

    private RegionResponse toResponse(Region region) {
        return RegionResponse.builder()
                .id(region.getId())
                .name(region.getName())
                .parentId(region.getParentId())
                .level(region.getLevel())
                .path(region.getPath())
                .treeType(region.getTreeType())
                .build();
    }
}
