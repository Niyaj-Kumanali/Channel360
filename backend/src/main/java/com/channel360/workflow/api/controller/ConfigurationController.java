package com.channel360.workflow.api.controller;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.workflow.api.dto.configuration.BusinessProcessRequestDTO;
import com.channel360.workflow.api.dto.configuration.BusinessProcessResponseDTO;
import com.channel360.workflow.api.dto.configuration.SegmentRequestDTO;
import com.channel360.workflow.api.dto.configuration.SegmentResponseDTO;
import com.channel360.workflow.application.configuration.BusinessProcessService;
import com.channel360.workflow.application.configuration.SegmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/configuration")
@RequiredArgsConstructor
public class ConfigurationController {

    private final BusinessProcessService businessProcessService;
    private final SegmentService segmentService;

    @GetMapping("/business-processes")
    public ResponseEntity<ApiResponse<List<BusinessProcessResponseDTO>>> getAllBusinessProcesses() {
        var list = businessProcessService.getAll().stream()
            .map(bp -> new BusinessProcessResponseDTO(bp.getId(), bp.getName(), bp.getCode(),
                bp.getDescription(), bp.getActive()))
            .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/business-processes/{id}")
    public ResponseEntity<ApiResponse<BusinessProcessResponseDTO>> getBusinessProcess(@PathVariable Long id) {
        var bp = businessProcessService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(
            new BusinessProcessResponseDTO(bp.getId(), bp.getName(), bp.getCode(),
                bp.getDescription(), bp.getActive())));
    }

    @PostMapping("/business-processes")
    public ResponseEntity<ApiResponse<BusinessProcessResponseDTO>> createBusinessProcess(
            @Valid @RequestBody BusinessProcessRequestDTO request) {
        var bp = businessProcessService.create(request.name(), request.code(), request.description());
        return ResponseEntity.ok(ApiResponse.success(
            new BusinessProcessResponseDTO(bp.getId(), bp.getName(), bp.getCode(),
                bp.getDescription(), bp.getActive())));
    }

    @PutMapping("/business-processes/{id}")
    public ResponseEntity<ApiResponse<BusinessProcessResponseDTO>> updateBusinessProcess(
            @PathVariable Long id, @Valid @RequestBody BusinessProcessRequestDTO request) {
        var bp = businessProcessService.update(id, request.name(), request.description());
        return ResponseEntity.ok(ApiResponse.success(
            new BusinessProcessResponseDTO(bp.getId(), bp.getName(), bp.getCode(),
                bp.getDescription(), bp.getActive())));
    }

    @DeleteMapping("/business-processes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBusinessProcess(@PathVariable Long id) {
        businessProcessService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted"));
    }

    @GetMapping("/segments")
    public ResponseEntity<ApiResponse<List<SegmentResponseDTO>>> getAllSegments() {
        var list = segmentService.getAll().stream()
            .map(s -> new SegmentResponseDTO(s.getId(), s.getName(), s.getCode(),
                s.getDescription(), s.getActive()))
            .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/segments/{id}")
    public ResponseEntity<ApiResponse<SegmentResponseDTO>> getSegment(@PathVariable Long id) {
        var s = segmentService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(
            new SegmentResponseDTO(s.getId(), s.getName(), s.getCode(),
                s.getDescription(), s.getActive())));
    }

    @PostMapping("/segments")
    public ResponseEntity<ApiResponse<SegmentResponseDTO>> createSegment(
            @Valid @RequestBody SegmentRequestDTO request) {
        var s = segmentService.create(request.name(), request.code(), request.description());
        return ResponseEntity.ok(ApiResponse.success(
            new SegmentResponseDTO(s.getId(), s.getName(), s.getCode(),
                s.getDescription(), s.getActive())));
    }

    @PutMapping("/segments/{id}")
    public ResponseEntity<ApiResponse<SegmentResponseDTO>> updateSegment(
            @PathVariable Long id, @Valid @RequestBody SegmentRequestDTO request) {
        var s = segmentService.update(id, request.name(), request.description());
        return ResponseEntity.ok(ApiResponse.success(
            new SegmentResponseDTO(s.getId(), s.getName(), s.getCode(),
                s.getDescription(), s.getActive())));
    }

    @DeleteMapping("/segments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSegment(@PathVariable Long id) {
        segmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted"));
    }
}
