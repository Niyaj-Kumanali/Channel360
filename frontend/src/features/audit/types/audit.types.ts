export interface AuditLog {
  id: number;
  userId: number | null;
  userName: string | null;
  userEmail: string | null;
  action: string;
  moduleName: string;
  entityName: string;
  entityId: number | null;
  oldData: string | null;
  newData: string | null;
  createdAt: string;
}
