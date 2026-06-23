export interface WorkflowStep {
  id: number;
  workflowId: number;
  stepOrder: number;
  roleName: string;
  label: string;
  mandatory: boolean;
  slaHours: number | null;
  escalationRole: string | null;
  description: string | null;
  createdBy: string;
  updatedBy: string;
}

export interface Workflow {
  id: number;
  name: string;
  description: string | null;
  module: string | null;
  active: boolean;
  createdBy: string;
  updatedBy: string;
  steps: WorkflowStep[];
}

export interface WorkflowRequest {
  name: string;
  description?: string | null;
  module?: string | null;
  active?: boolean;
}

export interface WorkflowStepRequest {
  workflowId: number;
  stepOrder: number;
  roleName: string;
  label: string;
  mandatory?: boolean;
  slaHours?: number | null;
  escalationRole?: string | null;
  description?: string | null;
}
