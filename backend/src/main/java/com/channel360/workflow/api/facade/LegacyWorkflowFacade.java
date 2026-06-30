package com.channel360.workflow.api.facade;

import com.channel360.workflow.api.dto.WorkflowResponse;
import com.channel360.workflow.api.dto.WorkflowStepResponse;
import java.util.List;

public interface LegacyWorkflowFacade {
    WorkflowResponse getById(Long id);
    String getWorkflowNameById(Long id);
    List<WorkflowStepResponse> getStepsByWorkflowId(Long workflowId);
    WorkflowStepResponse getStepById(Long id);
}
