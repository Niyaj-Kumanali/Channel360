package com.channel360.region.api;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.CustomUserDetails;
import com.channel360.common.security.RequirePermission;
import com.channel360.region.api.RegionRequest;
import com.channel360.region.api.RegionResponse;
import com.channel360.region.application.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    @RequirePermission("regions.view")
    public ResponseEntity<ApiResponse<List<RegionResponse>>> getAllRegions(
            @RequestParam(required = false) String treeType) {
        List<RegionResponse> regions = regionService.getAllRegions(treeType);
        return ResponseEntity.ok(ApiResponse.success(regions, "Regions retrieved successfully"));
    }

    @GetMapping("/{id}")
    @RequirePermission("regions.view")
    public ResponseEntity<ApiResponse<RegionResponse>> getRegionById(@PathVariable Long id) {
        RegionResponse region = regionService.getRegionById(id);
        return ResponseEntity.ok(ApiResponse.success(region, "Region retrieved successfully"));
    }

    @PostMapping
    @RequirePermission("regions.create")
    public ResponseEntity<ApiResponse<RegionResponse>> createRegion(
            @Valid @RequestBody RegionRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        RegionResponse region = regionService.createRegion(request, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(region, "Region created successfully"));
    }

    @PutMapping("/{id}")
    @RequirePermission("regions.edit")
    public ResponseEntity<ApiResponse<RegionResponse>> updateRegion(
            @PathVariable Long id,
            @Valid @RequestBody RegionRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        RegionResponse region = regionService.updateRegion(id, request, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(region, "Region updated successfully"));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("regions.delete")
    public ResponseEntity<ApiResponse<Void>> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Region deleted successfully"));
    }
}
