package com.channel360.approval.service;

import com.channel360.approval.dto.request.ApprovalActionRequest;
import com.channel360.approval.dto.request.ApprovalRequestCreate;
import com.channel360.approval.dto.response.ApprovalRequestResponse;
import com.channel360.approval.dto.response.ApprovalTaskResponse;
import com.channel360.approval.entity.ApprovalRequest;
import com.channel360.approval.entity.ApprovalTask;
import com.channel360.approval.repository.ApprovalRequestRepository;
import com.channel360.approval.repository.ApprovalTaskRepository;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.region.entity.Region;
import com.channel360.region.repository.RegionRepository;
import com.channel360.regionapprover.entity.RegionApprover;
import com.channel360.regionapprover.repository.RegionApproverRepository;
import com.channel360.role.entity.Role;
import com.channel360.role.repository.RoleRepository;
import com.channel360.user.entity.User;
import com.channel360.user.repository.UserRepository;
import com.channel360.workflow.entity.ApprovalWorkflow;
import com.channel360.workflow.entity.ApprovalWorkflowStep;
import com.channel360.workflow.repository.WorkflowRepository;
import com.channel360.workflow.repository.WorkflowStepRepository;
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
    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository stepRepository;
    private final RegionRepository regionRepository;
    private final RegionApproverRepository regionApproverRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

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
        workflowRepository.findActiveById(req.getWorkflowId())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", req.getWorkflowId()));

        ApprovalRequest request = ApprovalRequest.builder()
                .workflowId(req.getWorkflowId())
                .requestType(req.getRequestType())
                .requestReferenceId(req.getRequestReferenceId())
                .requestRegionId(req.getRequestRegionId())
                .requestorId(req.getRequestorId())
                .status("PENDING")
                .build();
        request = requestRepository.save(request);

        List<ApprovalWorkflowStep> steps = stepRepository
                .findByWorkflowIdAndDeletedFlagFalseOrderByStepOrder(req.getWorkflowId());

        List<ApprovalTask> tasks = new ArrayList<>();
        for (ApprovalWorkflowStep step : steps) {
            Long resolvedUserId = resolveApprover(req.getRequestRegionId(), step.getRoleName());
            Long resolvedRegionId = resolveApproverRegion(req.getRequestRegionId());

            Role role = roleRepository.findByName(step.getRoleName())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", step.getRoleName()));

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

        Region current = regionRepository.findActiveById(regionId).orElse(null);
        while (current != null && current.getParentId() != null) {
            Long parentId = current.getParentId();
            regionChain.add(parentId);
            Region parent = regionRepository.findActiveById(parentId).orElse(null);
            current = parent;
        }

        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) return null;

        for (Long rid : regionChain) {
            RegionApprover approver = regionApproverRepository
                    .findByRegionIdAndRoleIdAndUserIdAndActiveFlagTrue(rid, role.getId(), null)
                    .orElse(null);
            if (approver != null) return approver.getUserId();
        }

        return null;
    }

    private Long resolveApproverRegion(Long regionId) {
        if (regionId == null) return null;
        Region region = regionRepository.findActiveById(regionId).orElse(null);
        if (region == null) return null;

        List<Long> chain = new ArrayList<>();
        chain.add(regionId);
        Region current = region;
        while (current.getParentId() != null) {
            chain.add(current.getParentId());
            Region parent = regionRepository.findActiveById(current.getParentId()).orElse(null);
            if (parent == null) break;
            current = parent;
        }

        for (Long rid : chain) {
            if (regionApproverRepository.existsByRegionIdAndActiveFlagTrue(rid)) {
                return rid;
            }
        }
        return regionId;
    }

    private ApprovalRequestResponse toDto(ApprovalRequest request) {
        String workflowName = null;
        try {
            workflowName = workflowRepository.findActiveById(request.getWorkflowId())
                    .map(ApprovalWorkflow::getName).orElse(null);
        } catch (Exception ignored) {}

        String regionName = null;
        try {
            regionName = regionRepository.findActiveById(request.getRequestRegionId())
                    .map(Region::getName).orElse(null);
        } catch (Exception ignored) {}

        String requestorName = null;
        try {
            User u = userRepository.findById(request.getRequestorId()).orElse(null);
            if (u != null) requestorName = u.getFirstName() + " " + u.getLastName();
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
            ApprovalWorkflowStep step = stepRepository.findActiveById(task.getWorkflowStepId()).orElse(null);
            if (step != null) { stepLabel = step.getLabel(); stepOrder = step.getStepOrder(); }
        } catch (Exception ignored) {}

        String roleName = null;
        try {
            roleName = roleRepository.findById(task.getAssignedRoleId()).map(Role::getName).orElse(null);
        } catch (Exception ignored) {}

        String userName = null;
        try {
            if (task.getAssignedUserId() != null) {
                User u = userRepository.findById(task.getAssignedUserId()).orElse(null);
                if (u != null) userName = u.getFirstName() + " " + u.getLastName();
            }
        } catch (Exception ignored) {}

        String regionName = null;
        try {
            if (task.getAssignedRegionId() != null) {
                regionName = regionRepository.findActiveById(task.getAssignedRegionId()).map(Region::getName).orElse(null);
            }
        } catch (Exception ignored) {}

        String approvedByName = null;
        try {
            if (task.getApprovedBy() != null) {
                User u = userRepository.findById(task.getApprovedBy()).orElse(null);
                if (u != null) approvedByName = u.getFirstName() + " " + u.getLastName();
            }
        } catch (Exception ignored) {}

        String rejectedByName = null;
        try {
            if (task.getRejectedBy() != null) {
                User u = userRepository.findById(task.getRejectedBy()).orElse(null);
                if (u != null) rejectedByName = u.getFirstName() + " " + u.getLastName();
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
