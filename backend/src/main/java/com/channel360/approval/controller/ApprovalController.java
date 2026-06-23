package com.channel360.approval.controller;

import com.channel360.approval.dto.request.ApprovalActionRequest;
import com.channel360.approval.dto.request.ApprovalRequestCreate;
import com.channel360.approval.dto.response.ApprovalRequestResponse;
import com.channel360.approval.dto.response.ApprovalTaskResponse;
import com.channel360.approval.service.ApprovalService;
import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/approval-requests")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @GetMapping
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<List<ApprovalRequestResponse>>> getAllRequests() {
        List<ApprovalRequestResponse> requests = approvalService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success(requests, "Approval requests retrieved successfully"));
    }

    @GetMapping("/{id}")
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<ApprovalRequestResponse>> getRequestById(@PathVariable Long id) {
        ApprovalRequestResponse request = approvalService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(request, "Approval request retrieved successfully"));
    }

    @GetMapping("/my/{userId}")
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<List<ApprovalRequestResponse>>> getMyRequests(@PathVariable Long userId) {
        List<ApprovalRequestResponse> requests = approvalService.getMyRequests(userId);
        return ResponseEntity.ok(ApiResponse.success(requests, "Requests retrieved successfully"));
    }

    @PostMapping
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<ApprovalRequestResponse>> createRequest(
            @Valid @RequestBody ApprovalRequestCreate request) {
        ApprovalRequestResponse created = approvalService.createRequest(request);
        return ResponseEntity.ok(ApiResponse.success(created, "Approval request created successfully"));
    }

    @PostMapping("/{taskId}/approve")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<ApprovalTaskResponse>> approveTask(
            @PathVariable Long taskId,
            @Valid @RequestBody ApprovalActionRequest action) {
        ApprovalTaskResponse task = approvalService.approveTask(taskId, action);
        return ResponseEntity.ok(ApiResponse.success(task, "Task approved successfully"));
    }

    @PostMapping("/{taskId}/reject")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<ApprovalTaskResponse>> rejectTask(
            @PathVariable Long taskId,
            @Valid @RequestBody ApprovalActionRequest action) {
        ApprovalTaskResponse task = approvalService.rejectTask(taskId, action);
        return ResponseEntity.ok(ApiResponse.success(task, "Task rejected successfully"));
    }
}
