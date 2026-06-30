package com.channel360.workflow.api.controller;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import com.channel360.workflow.application.lifecycle.LifecycleQueryService;
import com.channel360.workflow.application.lifecycle.WorkflowCloneService;
import com.channel360.workflow.application.lifecycle.WorkflowLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowLifecycleController {

    private final WorkflowLifecycleService lifecycleService;
    private final WorkflowCloneService cloneService;
    private final LifecycleQueryService lifecycleQueryService;

    @PostMapping
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<Long>> createWorkflow(@RequestParam String name,
                                                             @RequestParam(required = false) String description) {
        var version = lifecycleService.createWorkflow(name, description);
        return ResponseEntity.ok(ApiResponse.success(version.getWorkflow().getId()));
    }

    @PostMapping("/{workflowId}/draft")
    @RequirePermission("workflows.design")
    public ResponseEntity<ApiResponse<Void>> createDraft(@PathVariable Long workflowId) {
        lifecycleService.createDraft(workflowId);
        return ResponseEntity.ok(ApiResponse.success(null, "Draft created"));
    }

    @PostMapping("/versions/{versionId}/publish")
    @RequirePermission("workflows.publish")
    public ResponseEntity<ApiResponse<Void>> publish(@PathVariable Long versionId) {
        lifecycleService.publish(versionId);
        return ResponseEntity.ok(ApiResponse.success(null, "Published"));
    }

    @PostMapping("/versions/{versionId}/archive")
    @RequirePermission("workflows.publish")
    public ResponseEntity<ApiResponse<Void>> archive(@PathVariable Long versionId) {
        lifecycleService.archive(versionId);
        return ResponseEntity.ok(ApiResponse.success(null, "Archived"));
    }

    @PostMapping("/{workflowId}/clone")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<Long>> cloneWorkflow(@PathVariable Long workflowId,
                                                            @RequestParam(required = false) String name) {
        var version = cloneService.cloneWorkflow(workflowId, name);
        return ResponseEntity.ok(ApiResponse.success(version.getWorkflow().getId()));
    }

    @PostMapping("/{workflowId}/activate")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Long workflowId) {
        lifecycleService.activate(workflowId);
        return ResponseEntity.ok(ApiResponse.success(null, "Activated"));
    }

    @PostMapping("/{workflowId}/deactivate")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long workflowId) {
        lifecycleService.deactivate(workflowId);
        return ResponseEntity.ok(ApiResponse.success(null, "Deactivated"));
    }

    @GetMapping("/{workflowId}/status")
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<Boolean>> getPublishedStatus(@PathVariable Long workflowId) {
        boolean published = lifecycleQueryService.isPublished(workflowId);
        return ResponseEntity.ok(ApiResponse.success(published));
    }
}
