package com.channel360.workflow.api.controller;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import com.channel360.workflow.api.dto.designer.WorkflowGraphDTO;
import com.channel360.workflow.api.mapper.WorkflowGraphMapper;
import com.channel360.workflow.application.designer.DesignerQueryService;
import com.channel360.workflow.application.designer.WorkflowDesignerService;
import com.channel360.workflow.application.designer.validation.ValidationResult;
import com.channel360.workflow.application.serialization.JsonSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowDesignerController {

    private final WorkflowDesignerService designerService;
    private final DesignerQueryService designerQueryService;
    private final WorkflowGraphMapper graphMapper;
    private final JsonSerializer jsonSerializer;

    @GetMapping("/{workflowId}/graph")
    @RequirePermission("workflows.design")
    public ResponseEntity<String> loadGraph(@PathVariable Long workflowId) {
        String json = designerService.loadGraphJson(workflowId);
        return ResponseEntity.ok(json);
    }

    @PostMapping("/{workflowId}/graph")
    @RequirePermission("workflows.design")
    public ResponseEntity<Void> saveGraph(@PathVariable Long workflowId,
                                           @RequestBody WorkflowGraphDTO.SaveRequest request) {
        var graph = graphMapper.toDomain(request.graph());
        designerService.saveGraph(workflowId, graph, request.nodeStates(), request.transitionStates());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{workflowId}/validate")
    @RequirePermission("workflows.design")
    public ResponseEntity<ValidationResult> validate(@PathVariable Long workflowId) {
        ValidationResult result = designerService.validateGraph(workflowId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{workflowId}/draft")
    @RequirePermission("workflows.design")
    public ResponseEntity<ApiResponse<Long>> getDraftVersion(@PathVariable Long workflowId) {
        if (designerQueryService.hasDraft(workflowId)) {
            var draft = designerQueryService.getDraftVersion(workflowId);
            return ResponseEntity.ok(ApiResponse.success(draft.getId()));
        }
        return ResponseEntity.ok(ApiResponse.success(null, "No draft version"));
    }

    @GetMapping("/{workflowId}/versions")
    @RequirePermission("workflows.design")
    public ResponseEntity<ApiResponse<Integer>> getVersionCount(@PathVariable Long workflowId) {
        var versions = designerQueryService.getVersions(workflowId);
        return ResponseEntity.ok(ApiResponse.success(versions.size()));
    }
}
