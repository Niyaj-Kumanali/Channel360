package com.channel360.workflow.application.runtime;

import com.channel360.workflow.api.dto.runtime.RequestResponseDTO;
import com.channel360.workflow.api.dto.runtime.TaskResponseDTO;
import com.channel360.workflow.api.dto.runtime.TimelineResponseDTO;
import com.channel360.workflow.domain.enums.TaskStatus;
import com.channel360.workflow.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowQueryService {

    private final WorkflowRequestRepository requestRepository;
    private final WorkflowTaskRepository taskRepository;
    private final WorkflowHistoryRepository historyRepository;

    public List<TimelineResponseDTO> getTimeline(Long requestId) {
        return historyRepository.findByRequestIdOrderByCreatedAtAsc(requestId)
            .stream()
            .map(h -> new TimelineResponseDTO(h.getId(), h.getAction().name(),
                h.getActorId(), h.getComments(), h.getCreatedAt()))
            .toList();
    }

    public List<TaskResponseDTO> getMyPendingTasks(Long userId) {
        return taskRepository.findByAssignedUserIdAndStatus(userId, TaskStatus.PENDING)
            .stream()
            .map(t -> new TaskResponseDTO(t.getId(), t.getRequest().getId(),
                t.getNode().getName(), t.getAssignedUserId(),
                t.getStatus().name(), t.getCreatedAt()))
            .toList();
    }

    public List<RequestResponseDTO> getUserRequests(Long userId) {
        return requestRepository.findByRequestorIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(r -> new RequestResponseDTO(r.getId(),
                r.getWorkflowVersion().getId(), r.getRequestType(),
                r.getRequestorId(), r.getStatus().name(), r.getCreatedAt()))
            .toList();
    }
}
