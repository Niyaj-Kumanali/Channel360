package com.channel360.regionapprover.controller;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.CustomUserDetails;
import com.channel360.common.security.RequirePermission;
import com.channel360.regionapprover.dto.request.RegionApproverRequest;
import com.channel360.regionapprover.dto.response.RegionApproverResponse;
import com.channel360.regionapprover.service.RegionApproverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/region-approvers")
@RequiredArgsConstructor
public class RegionApproverController {

    private final RegionApproverService regionApproverService;

    @GetMapping
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<List<RegionApproverResponse>>> getAllApprovers() {
        List<RegionApproverResponse> approvers = regionApproverService.getAllApprovers();
        return ResponseEntity.ok(ApiResponse.success(approvers, "Region approvers retrieved successfully"));
    }

    @GetMapping("/{id}")
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<RegionApproverResponse>> getApproverById(@PathVariable Long id) {
        RegionApproverResponse approver = regionApproverService.getApproverById(id);
        return ResponseEntity.ok(ApiResponse.success(approver, "Region approver retrieved successfully"));
    }

    @PostMapping
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<RegionApproverResponse>> createApprover(
            @Valid @RequestBody RegionApproverRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        RegionApproverResponse approver = regionApproverService.createApprover(request, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(approver, "Region approver created successfully"));
    }

    @PutMapping("/{id}")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<RegionApproverResponse>> updateApprover(
            @PathVariable Long id,
            @Valid @RequestBody RegionApproverRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        RegionApproverResponse approver = regionApproverService.updateApprover(id, request, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(approver, "Region approver updated successfully"));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<Void>> deactivateApprover(@PathVariable Long id) {
        regionApproverService.deactivateApprover(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Region approver deactivated successfully"));
    }
}
