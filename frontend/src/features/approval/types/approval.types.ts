export interface ApprovalTaskResponse {
  id: number;
  approvalRequestId: number;
  workflowStepId: number;
  stepLabel: string | null;
  stepOrder: number | null;
  assignedRoleId: number;
  assignedRoleName: string | null;
  assignedUserId: number | null;
  assignedUserName: string | null;
  assignedRegionId: number | null;
  assignedRegionName: string | null;
  status: string;
  approvedBy: number | null;
  approvedByName: string | null;
  approvedAt: string | null;
  rejectedBy: number | null;
  rejectedByName: string | null;
  rejectedAt: string | null;
  comments: string | null;
  createdAt: string;
}

export interface ApprovalRequestResponse {
  id: number;
  workflowId: number;
  workflowName: string | null;
  requestType: string;
  requestReferenceId: number | null;
  requestRegionId: number | null;
  requestRegionName: string | null;
  requestorId: number;
  requestorName: string | null;
  status: string;
  createdAt: string;
  updatedAt: string;
  tasks: ApprovalTaskResponse[];
}

export interface ApprovalRequestCreate {
  workflowId: number;
  requestType: string;
  requestReferenceId?: number | null;
  requestRegionId: number;
  requestorId: number;
}

export interface ApprovalActionRequest {
  userId: number;
  comments?: string;
}
