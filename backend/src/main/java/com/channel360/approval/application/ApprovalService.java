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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        workflowFacade.getById(req.getWorkflowId());

        ApprovalRequest request = ApprovalRequest.builder()
                .workflowId(req.getWorkflowId())
                .requestType(req.getRequestType())
                .requestReferenceId(req.getRequestReferenceId())
                .requestRegionId(req.getRequestRegionId())
                .requestorId(req.getRequestorId())
                .status("PENDING")
                .build();
        request = requestRepository.save(request);

        List<WorkflowStepResponse> steps = workflowFacade.getStepsByWorkflowId(req.getWorkflowId());

        List<ApprovalTask> tasks = new ArrayList<>();
        for (WorkflowStepResponse step : steps) {
            Long resolvedUserId = resolveApprover(req.getRequestRegionId(), step.getRoleName());
            Long resolvedRegionId = resolveApproverRegion(req.getRequestRegionId());

            RoleResponse role = roleFacade.findByName(step.getRoleName());

            ApprovalTask task = ApprovalTask.builder()
                    .approvalRequestId(request.getId())
                    .workflowStepId(step.getId())
                    .assignedRoleId(role.getId())
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
        task.setApprovedBy(action.getUserId());
        task.setApprovedAt(LocalDateTime.now());
        task.setComments(action.getComments());
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
        task.setRejectedBy(action.getUserId());
        task.setRejectedAt(LocalDateTime.now());
        task.setComments(action.getComments());
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
        boolean allApproved = tasks.stream().allMatch(t -> "APPROVED".equals(t.getStatus()));

        if (allApproved) {
            request.setStatus("APPROVED");
            requestRepository.save(request);
        }
    }

    private Long resolveApprover(Long regionId, String roleName) {
        if (regionId == null) return null;

        List<Long> regionChain = new ArrayList<>();
        regionChain.add(regionId);
        try {
            RegionResponse current = regionFacade.getById(regionId);
            while (current != null && current.getParentId() != null) {
                Long parentId = current.getParentId();
                regionChain.add(parentId);
                current = regionFacade.getById(parentId);
            }
        } catch (Exception e) {
            return null;
        }

        RoleResponse role;
        try {
            role = roleFacade.findByName(roleName);
        } catch (Exception e) {
            return null;
        }

        for (Long rid : regionChain) {
            Long userId = regionApproverFacade.findApproverUserId(rid, role.getId(), null);
            if (userId != null) return userId;
        }

        return null;
    }

    private Long resolveApproverRegion(Long regionId) {
        if (regionId == null) return null;
        try {
            regionFacade.getById(regionId);
        } catch (Exception e) {
            return null;
        }

        List<Long> chain = new ArrayList<>();
        chain.add(regionId);
        try {
            RegionResponse current = regionFacade.getById(regionId);
            while (current.getParentId() != null) {
                chain.add(current.getParentId());
                current = regionFacade.getById(current.getParentId());
            }
        } catch (Exception e) {
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
        String workflowName = null;
        try {
            workflowName = workflowFacade.getWorkflowNameById(request.getWorkflowId());
        } catch (Exception ignored) {}

        String regionName = null;
        try {
            regionName = regionFacade.getRegionNameById(request.getRequestRegionId());
        } catch (Exception ignored) {}

        String requestorName = null;
        try {
            UserResponse u = userFacade.getById(request.getRequestorId());
            requestorName = u.getFirstName() + " " + u.getLastName();
        } catch (Exception ignored) {}

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
            stepLabel = step.getLabel();
            stepOrder = step.getStepOrder();
        } catch (Exception ignored) {}

        String roleName = null;
        try {
            roleName = roleFacade.getRoleNameById(task.getAssignedRoleId());
        } catch (Exception ignored) {}

        String userName = null;
        try {
            if (task.getAssignedUserId() != null) {
                UserResponse u = userFacade.getById(task.getAssignedUserId());
                userName = u.getFirstName() + " " + u.getLastName();
            }
        } catch (Exception ignored) {}

        String regionName = null;
        try {
            if (task.getAssignedRegionId() != null) {
                regionName = regionFacade.getRegionNameById(task.getAssignedRegionId());
            }
        } catch (Exception ignored) {}

        String approvedByName = null;
        try {
            if (task.getApprovedBy() != null) {
                UserResponse u = userFacade.getById(task.getApprovedBy());
                approvedByName = u.getFirstName() + " " + u.getLastName();
            }
        } catch (Exception ignored) {}

        String rejectedByName = null;
        try {
            if (task.getRejectedBy() != null) {
                UserResponse u = userFacade.getById(task.getRejectedBy());
                rejectedByName = u.getFirstName() + " " + u.getLastName();
            }
        } catch (Exception ignored) {}

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
}
