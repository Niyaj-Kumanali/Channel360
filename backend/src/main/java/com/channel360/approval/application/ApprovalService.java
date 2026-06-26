package com.channel360.approval.application;

import com.channel360.approval.api.ApprovalActionRequest;
import com.channel360.approval.api.ApprovalRequestCreate;
import com.channel360.approval.api.ApprovalRequestResponse;
import com.channel360.approval.api.ApprovalTaskResponse;
import com.channel360.approval.domain.ApprovalRequest;
import com.channel360.approval.domain.ApprovalTask;
import com.channel360.approval.infrastructure.ApprovalRequestRepository;
import com.channel360.approval.infrastructure.ApprovalTaskRepository;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.region.api.RegionFacade;
import com.channel360.region.api.RegionResponse;
import com.channel360.regionapprover.api.RegionApproverFacade;
import com.channel360.role.api.RoleFacade;
import com.channel360.role.api.RoleResponse;
import com.channel360.user.api.UserFacade;
import com.channel360.user.api.UserResponse;
import com.channel360.workflow.api.WorkflowFacade;
import com.channel360.workflow.api.WorkflowStepResponse;
import com.channel360.workflow.domain.event.WorkflowApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalService {

    private final ApprovalRequestRepository requestRepository;
    private final ApprovalTaskRepository taskRepository;
    private final WorkflowFacade workflowFacade;
    private final RegionFacade regionFacade;
    private final RegionApproverFacade regionApproverFacade;
    private final RoleFacade roleFacade;
    private final UserFacade userFacade;
    private final ApplicationEventPublisher eventPublisher;

    public List<ApprovalRequestResponse> getAllRequests() {
        return requestRepository.findByStatusOrderByCreatedAtDesc(null).stream()
                .map(this::toDto)
                .toList();
    }

    public ApprovalRequestResponse getRequestById(Long id) {
        return requestRepository.findActiveById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Approval request", "id", id));
    }

    public List<ApprovalRequestResponse> getMyRequests(Long userId) {
        return requestRepository.findByRequestorIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ApprovalRequestResponse createRequest(ApprovalRequestCreate req) {
        workflowFacade.getById(req.workflowId());

        ApprovalRequest request = ApprovalRequest.builder()
                .workflowId(req.workflowId())
                .requestType(req.requestType())
                .requestReferenceId(req.requestReferenceId())
                .requestRegionId(req.requestRegionId())
                .requestorId(req.requestorId())
                .status("PENDING")
                .build();
        request = requestRepository.save(request);

        List<WorkflowStepResponse> steps = workflowFacade.getStepsByWorkflowId(req.workflowId());

        List<ApprovalTask> tasks = new ArrayList<>();
        for (WorkflowStepResponse step : steps) {
            Long resolvedUserId = resolveApprover(req.requestRegionId(), step.roleName());
            Long resolvedRegionId = resolveApproverRegion(req.requestRegionId());

            RoleResponse role = roleFacade.findByName(step.roleName());

            ApprovalTask task = ApprovalTask.builder()
                    .approvalRequestId(request.getId())
                    .workflowStepId(step.id())
                    .assignedRoleId(role.id())
                    .assignedUserId(resolvedUserId)
                    .assignedRegionId(resolvedRegionId)
                    .status("PENDING")
                    .build();
            tasks.add(taskRepository.save(task));
        }

        return toDto(request);
    }

    @Transactional
    public ApprovalTaskResponse approveTask(Long taskId, ApprovalActionRequest action) {
        ApprovalTask task = taskRepository.findActiveById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval task", "id", taskId));

        if (!"PENDING".equals(task.getStatus())) {
            throw new IllegalStateException("Task is not in PENDING status");
        }

        task.setStatus("APPROVED");
        task.setApprovedBy(action.userId());
        task.setApprovedAt(LocalDateTime.now());
        task.setComments(action.comments());
        task = taskRepository.save(task);

        checkAndUpdateRequestStatus(task.getApprovalRequestId());

        return toTaskDto(task);
    }

    @Transactional
    public ApprovalTaskResponse rejectTask(Long taskId, ApprovalActionRequest action) {
        ApprovalTask task = taskRepository.findActiveById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval task", "id", taskId));

        if (!"PENDING".equals(task.getStatus())) {
            throw new IllegalStateException("Task is not in PENDING status");
        }

        task.setStatus("REJECTED");
        task.setRejectedBy(action.userId());
        task.setRejectedAt(LocalDateTime.now());
        task.setComments(action.comments());
        Long reqId = task.getApprovalRequestId();
        task = taskRepository.save(task);

        ApprovalRequest request = requestRepository.findActiveById(reqId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval request", "id", reqId));
        request.setStatus("REJECTED");
        requestRepository.save(request);

        return toTaskDto(task);
    }

    private void checkAndUpdateRequestStatus(Long requestId) {
        ApprovalRequest request = requestRepository.findActiveById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval request", "id", requestId));

        List<ApprovalTask> tasks = taskRepository.findByApprovalRequestIdOrderByCreatedAtAsc(requestId);
        boolean anyRejected = tasks.stream().anyMatch(t -> "REJECTED".equals(t.getStatus()));
        boolean allApproved = tasks.stream().allMatch(t -> "APPROVED".equals(t.getStatus()));

        if (anyRejected) {
            request.setStatus("REJECTED");
            requestRepository.save(request);
        } else if (allApproved) {
            request.setStatus("APPROVED");
            requestRepository.save(request);
            eventPublisher.publishEvent(new WorkflowApprovedEvent(requestId, null, null));
        }
    }

    private Long resolveApprover(Long regionId, String roleName) {
        if (regionId == null) return null;

        List<Long> regionChain = new ArrayList<>();
        regionChain.add(regionId);
        try {
            RegionResponse current = regionFacade.getById(regionId);
            while (current != null && current.parentId() != null) {
                Long parentId = current.parentId();
                regionChain.add(parentId);
                current = regionFacade.getById(parentId);
            }
        } catch (Exception e) {
            log.warn("Failed to resolve region chain for regionId {}: {}", regionId, e.getMessage());
            return null;
        }

        RoleResponse role;
        try {
            role = roleFacade.findByName(roleName);
        } catch (Exception e) {
            log.warn("Failed to find role by name {}: {}", roleName, e.getMessage());
            return null;
        }

        for (Long rid : regionChain) {
            Long userId = regionApproverFacade.findApproverUserId(rid, role.id(), null);
            if (userId != null) return userId;
        }

        return null;
    }

    private Long resolveApproverRegion(Long regionId) {
        if (regionId == null) return null;

        List<Long> chain = new ArrayList<>();
        chain.add(regionId);
        try {
            RegionResponse current = regionFacade.getById(regionId);
            while (current.parentId() != null) {
                chain.add(current.parentId());
                current = regionFacade.getById(current.parentId());
            }
        } catch (Exception e) {
            log.warn("Failed to resolve region chain for regionId {}: {}", regionId, e.getMessage());
            chain = List.of(regionId);
        }

        for (Long rid : chain) {
            if (regionApproverFacade.existsByRegionId(rid)) {
                return rid;
            }
        }
        return regionId;
    }

    private ApprovalRequestResponse toDto(ApprovalRequest request) {
        String workflowName = resolveWorkflowName(request.getWorkflowId());
        String regionName = resolveRegionName(request.getRequestRegionId());
        String requestorName = resolveUserName(request.getRequestorId());

        List<ApprovalTaskResponse> tasks = taskRepository
                .findByApprovalRequestIdOrderByCreatedAtAsc(request.getId())
                .stream().map(this::toTaskDto).toList();

        return ApprovalRequestResponse.builder()
                .id(request.getId())
                .workflowId(request.getWorkflowId())
                .workflowName(workflowName)
                .requestType(request.getRequestType())
                .requestReferenceId(request.getRequestReferenceId())
                .requestRegionId(request.getRequestRegionId())
                .requestRegionName(regionName)
                .requestorId(request.getRequestorId())
                .requestorName(requestorName)
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .tasks(tasks)
                .build();
    }

    private ApprovalTaskResponse toTaskDto(ApprovalTask task) {
        String stepLabel = null;
        Integer stepOrder = null;
        try {
            WorkflowStepResponse step = workflowFacade.getStepById(task.getWorkflowStepId());
            stepLabel = step.label();
            stepOrder = step.stepOrder();
        } catch (Exception e) {
            log.warn("Failed to resolve step {} for task {}: {}", task.getWorkflowStepId(), task.getId(), e.getMessage());
        }

        String roleName = resolveRoleName(task.getAssignedRoleId());
        String userName = resolveUserName(task.getAssignedUserId());
        String regionName = resolveRegionName(task.getAssignedRegionId());
        String approvedByName = resolveUserName(task.getApprovedBy());
        String rejectedByName = resolveUserName(task.getRejectedBy());

        return ApprovalTaskResponse.builder()
                .id(task.getId())
                .approvalRequestId(task.getApprovalRequestId())
                .workflowStepId(task.getWorkflowStepId())
                .stepLabel(stepLabel)
                .stepOrder(stepOrder)
                .assignedRoleId(task.getAssignedRoleId())
                .assignedRoleName(roleName)
                .assignedUserId(task.getAssignedUserId())
                .assignedUserName(userName)
                .assignedRegionId(task.getAssignedRegionId())
                .assignedRegionName(regionName)
                .status(task.getStatus())
                .approvedBy(task.getApprovedBy())
                .approvedByName(approvedByName)
                .approvedAt(task.getApprovedAt())
                .rejectedBy(task.getRejectedBy())
                .rejectedByName(rejectedByName)
                .rejectedAt(task.getRejectedAt())
                .comments(task.getComments())
                .createdAt(task.getCreatedAt())
                .build();
    }

    private String resolveWorkflowName(Long workflowId) {
        if (workflowId == null) return null;
        try {
            return workflowFacade.getWorkflowNameById(workflowId);
        } catch (Exception e) {
            log.warn("Failed to resolve workflow name for id {}: {}", workflowId, e.getMessage());
            return null;
        }
    }

    private String resolveRegionName(Long regionId) {
        if (regionId == null) return null;
        try {
            return regionFacade.getRegionNameById(regionId);
        } catch (Exception e) {
            log.warn("Failed to resolve region name for id {}: {}", regionId, e.getMessage());
            return null;
        }
    }

    private String resolveUserName(Long userId) {
        if (userId == null) return null;
        try {
            UserResponse u = userFacade.getById(userId);
            return u.firstName() + " " + u.lastName();
        } catch (Exception e) {
            log.warn("Failed to resolve user name for id {}: {}", userId, e.getMessage());
            return null;
        }
    }

    private String resolveRoleName(Long roleId) {
        if (roleId == null) return null;
        try {
            return roleFacade.getRoleNameById(roleId);
        } catch (Exception e) {
            log.warn("Failed to resolve role name for id {}: {}", roleId, e.getMessage());
            return null;
        }
    }
}
