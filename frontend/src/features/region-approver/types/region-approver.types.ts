export interface RegionApprover {
  id: number;
  regionId: number;
  regionName: string;
  regionPath: string;
  roleId: number;
  roleName: string;
  userId: number;
  userName: string;
  userEmail: string;
  activeFlag: boolean;
  createdBy: string;
  updatedBy: string;
}

export interface RegionApproverRequest {
  regionId: number;
  roleId: number;
  userId: number;
}
