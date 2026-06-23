package com.channel360.workflow.api;

import java.util.List;

public interface WorkflowFacade {
    WorkflowResponse getById(Long id);
    String getWorkflowNameById(Long id);
    List<WorkflowStepResponse> getStepsByWorkflowId(Long workflowId);
    WorkflowStepResponse getStepById(Long id);
}
