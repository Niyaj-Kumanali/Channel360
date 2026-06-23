package com.channel360.workflow.domain.event;

import com.channel360.workflow.domain.ApprovalWorkflow;
import lombok.Getter;

@Getter
public class WorkflowCreatedEvent {
    private final Long workflowId;
    private final String name;
    private final String module;

    public WorkflowCreatedEvent(ApprovalWorkflow workflow) {
        this.workflowId = workflow.getId();
        this.name = workflow.getName();
        this.module = workflow.getModule();
    }
}
