package com.channel360.workflow.api.controller;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import com.channel360.workflow.api.dto.runtime.*;
import com.channel360.workflow.application.runtime.NormalExecutionService;
import com.channel360.workflow.application.runtime.RequestService;
import com.channel360.workflow.application.runtime.WorkflowQueryService;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class WorkflowRequestController {

    private final NormalExecutionService executionService;
    private final RequestService requestService;
    private final WorkflowQueryService queryService;

    @PostMapping
    @RequirePermission("workflows.submit")
    public ResponseEntity<ApiResponse<Void>> submit(@RequestBody RequestSubmitDTO request) {
        BusinessContext ctx = new BusinessContext(request.businessContext());
        executionService.startWorkflow(request.workflowId(), ctx, request.idempotencyKey());
        return ResponseEntity.ok(ApiResponse.success(null, "Workflow started"));
    }

    @GetMapping("/{requestId}")
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<RequestResponseDTO>> getRequest(@PathVariable Long requestId) {
        var req = requestService.getRequest(requestId);
        return ResponseEntity.ok(ApiResponse.success(new RequestResponseDTO(req.getId(),
            req.getWorkflowVersion().getId(), req.getRequestType(),
            req.getRequestorId(), req.getStatus().name(), req.getCreatedAt())));
    }

    @GetMapping("/{requestId}/timeline")
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<List<TimelineResponseDTO>>> getTimeline(@PathVariable Long requestId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getTimeline(requestId)));
    }

    @GetMapping("/user/{userId}")
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<List<RequestResponseDTO>>> getUserRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getUserRequests(userId)));
    }
}
