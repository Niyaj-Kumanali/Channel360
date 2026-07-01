package com.channel360.audit.api;

import com.channel360.audit.api.response.AuditLogResponse;
import com.channel360.audit.application.AuditService;
import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @RequirePermission("audit.view")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAllLogs(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long userId) {
        List<AuditLogResponse> logs = auditService.getAllLogs(module, action, userId);
        return ResponseEntity.ok(ApiResponse.success(logs, "Audit logs retrieved successfully"));
    }
}
