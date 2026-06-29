package com.channel360.region.application;

import com.channel360.common.exception.BadRequestException;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.region.api.RegionRequest;
import com.channel360.region.api.RegionResponse;
import com.channel360.region.domain.Region;
import com.channel360.region.infrastructure.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
        validateLevel(request.level());
        validateTreeType(request.treeType());
        validateParent(request.parentId());

        regionRepository.spSave(null, request.name(), request.parentId(),
                request.level(), request.treeType(), user);

        Region saved = regionRepository.findActiveByName(request.name())
                .orElseThrow(() -> new ResourceNotFoundException("Region", "name", request.name()));
        return toDto(saved);
    }

    @Transactional
    public RegionResponse updateRegion(Long id, RegionRequest request, String user) {
        Region region = regionRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));

        if (request.name() != null) validateLevel(request.level());
        if (request.treeType() != null) validateTreeType(request.treeType());

        regionRepository.spSave(id, request.name(), request.parentId(),
                request.level(), request.treeType(), user);

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
        if (Arrays.stream(VALID_LEVELS).noneMatch(level::equals)) {
            throw new BadRequestException("Invalid level: " + level + ". Must be Zone, Region, State, or Territory");
        }
    }

    private void validateTreeType(String treeType) {
        if (treeType == null) return;
        if (Arrays.stream(VALID_TREE_TYPES).noneMatch(treeType::equals)) {
            throw new BadRequestException("Invalid tree type: " + treeType + ". Must be B2B or B2C");
        }
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
