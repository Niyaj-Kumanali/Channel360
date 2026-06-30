package com.channel360.workflow.application.runtime;

import com.channel360.workflow.application.engine.provider.WorkflowDefinitionProvider;
import com.channel360.workflow.domain.entity.WorkflowRequest;
import com.channel360.workflow.domain.entity.WorkflowVersion;
import com.channel360.workflow.domain.enums.RequestStatus;
import com.channel360.workflow.domain.enums.VersionStatus;
import com.channel360.workflow.domain.exception.WorkflowNotFoundException;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowNodeRepository;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowRequestRepository;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final WorkflowRequestRepository requestRepository;
    private final WorkflowVersionRepository versionRepository;
    private final WorkflowNodeRepository nodeRepository;
    private final WorkflowDefinitionProvider definitionProvider;

    @Transactional
    public WorkflowRequest createRequest(Long workflowId, String requestType,
                                          Long requestorId, BusinessContext ctx,
                                          String idempotencyKey) {
        WorkflowVersion version = versionRepository
            .findTopByWorkflowIdAndStatusOrderByVersionNumberDesc(workflowId, VersionStatus.PUBLISHED)
            .orElseThrow(() -> new WorkflowNotFoundException("No published version for workflow " + workflowId));

        WorkflowRequest request = WorkflowRequest.builder()
            .workflowVersion(version).requestType(requestType)
            .requestorId(requestorId).status(RequestStatus.PENDING)
            .metadataJson(ctx != null ? ctx.values().toString() : null)
            .idempotencyKey(idempotencyKey).build();
        return requestRepository.save(request);
    }

    public WorkflowRequest getRequest(Long requestId) {
        return requestRepository.findById(requestId)
            .orElseThrow(() -> new WorkflowNotFoundException("Request not found: " + requestId));
    }

    public List<WorkflowRequest> getRequestsByUser(Long userId) {
        return requestRepository.findByRequestorIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void updateStatus(WorkflowRequest request, RequestStatus newStatus) {
        request.setStatus(newStatus);
        requestRepository.save(request);
    }

    @Transactional
    public void updateCurrentNode(WorkflowRequest request, UUID currentNodeUuid) {
        nodeRepository.findByNodeUuid(currentNodeUuid).ifPresent(request::setCurrentNode);
    }
}
