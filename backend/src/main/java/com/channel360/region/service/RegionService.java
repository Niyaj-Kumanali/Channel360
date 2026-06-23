package com.channel360.region.service;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.region.dto.request.RegionRequest;
import com.channel360.region.dto.response.RegionResponse;
import com.channel360.region.entity.Region;
import com.channel360.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

    private static final String[] VALID_LEVELS = {"Zone", "Region", "State", "Territory"};
    private static final String[] VALID_TREE_TYPES = {"B2B", "B2C"};

    private final RegionRepository regionRepository;

    public List<RegionResponse> getAllRegions(String treeType) {
        List<Region> regions = treeType != null
                ? regionRepository.findByDeletedFlagFalseAndTreeTypeOrderByPath(treeType)
                : regionRepository.findByDeletedFlagFalseOrderByPath();
        return regions.stream()
                .map(this::toDto)
                .toList();
    }

    public RegionResponse getRegionById(Long id) {
        return regionRepository.findActiveById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));
    }

    @Transactional
    public RegionResponse createRegion(RegionRequest request, String user) {
        validateLevel(request.getLevel());
        validateTreeType(request.getTreeType());
        validateParent(request.getParentId());

        regionRepository.spSave(null, request.getName(), request.getParentId(),
                request.getLevel(), request.getTreeType(), user);

        Region saved = regionRepository.findActiveByName(request.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Region", "name", request.getName()));
        return toDto(saved);
    }

    @Transactional
    public RegionResponse updateRegion(Long id, RegionRequest request, String user) {
        Region region = regionRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));

        if (request.getName() != null) validateLevel(request.getLevel());
        if (request.getTreeType() != null) validateTreeType(request.getTreeType());

        regionRepository.spSave(id, request.getName(), request.getParentId(),
                request.getLevel(), request.getTreeType(), user);

        return regionRepository.findActiveById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));
    }

    @Transactional
    public void deleteRegion(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Region", "id", id);
        }
        regionRepository.spDelete(id);
    }

    private void validateLevel(String level) {
        if (level == null) return;
        for (String valid : VALID_LEVELS) {
            if (valid.equals(level)) return;
        }
        throw new IllegalArgumentException("Invalid level: " + level + ". Must be Zone, Region, State, or Territory");
    }

    private void validateTreeType(String treeType) {
        if (treeType == null) return;
        for (String valid : VALID_TREE_TYPES) {
            if (valid.equals(treeType)) return;
        }
        throw new IllegalArgumentException("Invalid tree type: " + treeType + ". Must be B2B or B2C");
    }

    private void validateParent(Long parentId) {
        if (parentId == null) return;
        regionRepository.findActiveById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent region", "id", parentId));
    }

    private RegionResponse toDto(Region region) {
        return RegionResponse.builder()
                .id(region.getId())
                .name(region.getName())
                .parentId(region.getParentId())
                .level(region.getLevel())
                .treeType(region.getTreeType())
                .path(region.getPath())
                .createdBy(region.getCreatedBy())
                .updatedBy(region.getUpdatedBy())
                .build();
    }
}
