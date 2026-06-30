package com.channel360.workflow.api.facade;

import com.channel360.workflow.api.dto.facade.PendingApprovalSummaryDTO;
import com.channel360.workflow.api.dto.facade.WorkflowLookupDTO;
import com.channel360.workflow.api.dto.facade.WorkflowStatusDTO;
import java.util.List;

public interface WorkflowFacade {
    WorkflowLookupDTO findWorkflow(Long workflowId);
    List<PendingApprovalSummaryDTO> findPendingTasks(Long userId);
    boolean hasPendingApproval(Long requestId);
    String getRequestStatus(Long requestId);
}
