package com.channel360.workflow.api;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.CustomUserDetails;
import com.channel360.common.security.RequirePermission;
import com.channel360.workflow.api.WorkflowRequest;
import com.channel360.workflow.api.WorkflowStepRequest;
import com.channel360.workflow.api.WorkflowResponse;
import com.channel360.workflow.api.WorkflowStepResponse;
import com.channel360.workflow.application.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @GetMapping
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<List<WorkflowResponse>>> getAllWorkflows() {
        List<WorkflowResponse> workflows = workflowService.getAllWorkflows();
        return ResponseEntity.ok(ApiResponse.success(workflows, "Workflows retrieved successfully"));
    }

    @GetMapping("/{id}")
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<WorkflowResponse>> getWorkflowById(@PathVariable Long id) {
        WorkflowResponse workflow = workflowService.getWorkflowById(id);
        return ResponseEntity.ok(ApiResponse.success(workflow, "Workflow retrieved successfully"));
    }

    @PostMapping
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<WorkflowResponse>> createWorkflow(
            @Valid @RequestBody WorkflowRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        WorkflowResponse workflow = workflowService.createWorkflow(request, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(workflow, "Workflow created successfully"));
    }

    @PutMapping("/{id}")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<WorkflowResponse>> updateWorkflow(
            @PathVariable Long id,
            @Valid @RequestBody WorkflowRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        WorkflowResponse workflow = workflowService.updateWorkflow(id, request, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(workflow, "Workflow updated successfully"));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<Void>> deleteWorkflow(@PathVariable Long id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Workflow deleted successfully"));
    }

    @PostMapping("/steps")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<WorkflowStepResponse>> addStep(
            @Valid @RequestBody WorkflowStepRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        WorkflowStepResponse step = workflowService.addStep(request, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(step, "Step added successfully"));
    }

    @PutMapping("/steps/{stepId}")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<WorkflowStepResponse>> updateStep(
            @PathVariable Long stepId,
            @Valid @RequestBody WorkflowStepRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        WorkflowStepResponse step = workflowService.updateStep(stepId, request, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(step, "Step updated successfully"));
    }

    @DeleteMapping("/steps/{stepId}")
    @RequirePermission("workflows.configure")
    public ResponseEntity<ApiResponse<Void>> deleteStep(@PathVariable Long stepId) {
        workflowService.deleteStep(stepId);
        return ResponseEntity.ok(ApiResponse.success(null, "Step deleted successfully"));
    }
}
