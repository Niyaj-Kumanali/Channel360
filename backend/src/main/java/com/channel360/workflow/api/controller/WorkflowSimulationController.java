package com.channel360.workflow.api.controller;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import com.channel360.workflow.api.dto.simulator.SimulateRequestDTO;
import com.channel360.workflow.api.dto.simulator.SimulateResultDTO;
import com.channel360.workflow.application.runtime.WorkflowSimulator;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulate")
@RequiredArgsConstructor
public class WorkflowSimulationController {

    private final WorkflowSimulator simulator;

    @PostMapping
    @RequirePermission("workflows.simulate")
    public ResponseEntity<ApiResponse<SimulateResultDTO>> simulate(@RequestBody SimulateRequestDTO request) {
        BusinessContext ctx = new BusinessContext(request.businessContext());
        var result = simulator.simulate(request.workflowVersionId(), ctx);
        return ResponseEntity.ok(ApiResponse.success(new SimulateResultDTO(
            result.finalResult().terminal(),
            result.finalResult().message(),
            result.trace().stream()
                .map(t -> t.fromNode().name() + " -> " + t.toNode().name())
                .toList()
        )));
    }
}
