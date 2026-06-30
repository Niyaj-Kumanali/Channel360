package com.channel360.workflow.api.controller;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import com.channel360.workflow.api.dto.runtime.TaskActionDTO;
import com.channel360.workflow.api.dto.runtime.TaskResponseDTO;
import com.channel360.workflow.application.runtime.NormalExecutionService;
import com.channel360.workflow.application.runtime.WorkflowQueryService;
import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class WorkflowTaskController {

    private final NormalExecutionService executionService;
    private final WorkflowQueryService queryService;

    @GetMapping("/pending/{userId}")
    @RequirePermission("workflows.view")
    public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getPendingTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getMyPendingTasks(userId)));
    }

    @PostMapping("/{taskId}/action")
    @RequirePermission("workflows.act")
    public ResponseEntity<ApiResponse<Void>> actOnTask(@PathVariable Long taskId,
                                                        @RequestBody TaskActionDTO action) {
        TransitionAction transitionAction = TransitionAction.valueOf(action.action().toUpperCase());
        BusinessContext ctx = new BusinessContext(action.businessContext());
        executionService.executeAction(taskId, transitionAction, action.delegatedUserId(),
            ctx, action.idempotencyKey());
        return ResponseEntity.ok(ApiResponse.success(null, "Action executed"));
    }
}
