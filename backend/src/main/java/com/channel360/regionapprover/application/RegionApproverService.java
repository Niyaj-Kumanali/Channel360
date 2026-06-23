package com.channel360.regionapprover.application;

import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.region.domain.Region;
import com.channel360.region.infrastructure.RegionRepository;
import com.channel360.regionapprover.api.RegionApproverRequest;
import com.channel360.regionapprover.api.RegionApproverResponse;
import com.channel360.regionapprover.domain.RegionApprover;
import com.channel360.regionapprover.infrastructure.RegionApproverRepository;
import com.channel360.role.domain.Role;
import com.channel360.role.infrastructure.RoleRepository;
import com.channel360.user.domain.User;
import com.channel360.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionApproverService {

    private final RegionApproverRepository regionApproverRepository;
    private final RegionRepository regionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public List<RegionApproverResponse> getAllApprovers() {
        return regionApproverRepository.findByActiveFlagTrueOrderByRegionIdAsc().stream()
                .map(this::toDto)
                .toList();
    }

    public RegionApproverResponse getApproverById(Long id) {
        return regionApproverRepository.findActiveById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Region approver", "id", id));
    }

    @Transactional
    public RegionApproverResponse createApprover(RegionApproverRequest request, String user) {
        regionRepository.findActiveById(request.getRegionId())
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", request.getRegionId()));
        roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", request.getRoleId()));
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        regionApproverRepository.spSave(null, request.getRegionId(), request.getRoleId(),
                request.getUserId(), true, user);

        RegionApprover saved = regionApproverRepository
                .findByRegionIdAndRoleIdAndUserIdAndActiveFlagTrue(
                        request.getRegionId(), request.getRoleId(), request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Region approver", "regionId", request.getRegionId()));
        return toDto(saved);
    }

    @Transactional
    public RegionApproverResponse updateApprover(Long id, RegionApproverRequest request, String user) {
        regionApproverRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region approver", "id", id));

        regionApproverRepository.spSave(id, request.getRegionId(), request.getRoleId(),
                request.getUserId(), true, user);

        return regionApproverRepository.findActiveById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Region approver", "id", id));
    }

    @Transactional
    public void deactivateApprover(Long id) {
        if (!regionApproverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Region approver", "id", id);
        }
        regionApproverRepository.spDeactivate(id);
    }

    private RegionApproverResponse toDto(RegionApprover ra) {
        String regionName = null;
        String regionPath = null;
        try {
            Region region = regionRepository.findActiveById(ra.getRegionId()).orElse(null);
            if (region != null) {
                regionName = region.getName();
                regionPath = region.getPath();
            }
        } catch (Exception ignored) {}

        String roleName = null;
        try {
            Role role = roleRepository.findById(ra.getRoleId()).orElse(null);
            if (role != null) roleName = role.getName();
        } catch (Exception ignored) {}

        String userName = null;
        String userEmail = null;
        try {
            User u = userRepository.findById(ra.getUserId()).orElse(null);
            if (u != null) {
                userName = u.getFirstName() + " " + u.getLastName();
                userEmail = u.getEmail();
            }
        } catch (Exception ignored) {}

        return RegionApproverResponse.builder()
                .id(ra.getId())
                .regionId(ra.getRegionId())
                .regionName(regionName)
                .regionPath(regionPath)
                .roleId(ra.getRoleId())
                .roleName(roleName)
                .userId(ra.getUserId())
                .userName(userName)
                .userEmail(userEmail)
                .activeFlag(ra.getActiveFlag())
                .createdBy(ra.getCreatedBy())
                .updatedBy(ra.getUpdatedBy())
                .build();
    }
}
