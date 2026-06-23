package com.channel360.workflow.domain.event;

import lombok.Getter;

@Getter
public class WorkflowApprovedEvent {
    private final Long requestId;
    private final Long stepId;
    private final Long approvedBy;

    public WorkflowApprovedEvent(Long requestId, Long stepId, Long approvedBy) {
        this.requestId = requestId;
        this.stepId = stepId;
        this.approvedBy = approvedBy;
    }
}
