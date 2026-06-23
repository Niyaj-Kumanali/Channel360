import React, { useEffect, useState } from 'react';
import { Search } from 'lucide-react';
import toast from 'react-hot-toast';
import { auditApi } from '@/features/audit/api/audit.api';
import type { AuditLog } from '@/features/audit/types/audit.types';
import { Loader } from '@/components/ui/Loader';
import { cn } from '@/lib/utils';

const actionColors: Record<string, string> = {
  CREATE: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
  UPDATE: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
  DELETE: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
};

export const AuditLogListPage: React.FC = () => {
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [moduleFilter, setModuleFilter] = useState('');
  const [actionFilter, setActionFilter] = useState('');
  const [selectedLog, setSelectedLog] = useState<AuditLog | null>(null);

  const fetchLogs = async () => {
    try {
      const params: Record<string, string> = {};
      if (moduleFilter) params.module = moduleFilter;
      if (actionFilter) params.action = actionFilter;
      const res = await auditApi.getAll(Object.keys(params).length ? params : undefined);
      if (res.success) setLogs(res.data);
    } catch { toast.error('Failed to load audit logs'); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchLogs(); }, [moduleFilter, actionFilter]);

  const modules = [...new Set(logs.map(l => l.moduleName))].sort();

  const formatJson = (json: string | null) => {
    if (!json) return null;
    try { return JSON.stringify(JSON.parse(json), null, 2); }
    catch { return json; }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-6xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Audit Logs</h1>
          <p className="text-sm text-muted-foreground mt-1">Track all platform changes with before/after values</p>
        </div>
      </div>

      <div className="flex flex-wrap gap-3 mb-6">
        <div className="relative flex-1 min-w-[200px] max-w-xs">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <select value={moduleFilter} onChange={e => setModuleFilter(e.target.value)}
            className="w-full rounded-lg border border-input bg-background pl-9 pr-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring appearance-none">
            <option value="">All Modules</option>
            {modules.map(m => <option key={m} value={m}>{m}</option>)}
          </select>
        </div>
        <select value={actionFilter} onChange={e => setActionFilter(e.target.value)}
          className="rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring">
          <option value="">All Actions</option>
          <option value="CREATE">CREATE</option>
          <option value="UPDATE">UPDATE</option>
          <option value="DELETE">DELETE</option>
        </select>
        <span className="text-sm text-muted-foreground self-center">{logs.length} entries</span>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <div className="rounded-xl border border-border bg-card overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border bg-muted/50">
                    <th className="text-left px-4 py-3 font-medium text-muted-foreground">Timestamp</th>
                    <th className="text-left px-4 py-3 font-medium text-muted-foreground">User</th>
                    <th className="text-left px-4 py-3 font-medium text-muted-foreground">Module</th>
                    <th className="text-left px-4 py-3 font-medium text-muted-foreground">Action</th>
                    <th className="text-left px-4 py-3 font-medium text-muted-foreground">Entity</th>
                  </tr>
                </thead>
                <tbody>
                  {logs.length === 0 ? (
                    <tr><td colSpan={5} className="px-4 py-12 text-center text-muted-foreground">No audit logs found.</td></tr>
                  ) : (
                    logs.map(log => (
                      <tr key={log.id}
                        onClick={() => setSelectedLog(selectedLog?.id === log.id ? null : log)}
                        className={cn(
                          'border-b border-border last:border-0 cursor-pointer transition-colors',
                          selectedLog?.id === log.id ? 'bg-primary/5' : 'hover:bg-muted/30'
                        )}>
                        <td className="px-4 py-3 text-muted-foreground text-xs whitespace-nowrap">
                          {new Date(log.createdAt).toLocaleString()}
                        </td>
                        <td className="px-4 py-3">
                          <span className="text-foreground">{log.userName || log.userEmail || `#${log.userId}`}</span>
                        </td>
                        <td className="px-4 py-3">
                          <span className="text-foreground capitalize">{log.moduleName}</span>
                        </td>
                        <td className="px-4 py-3">
                          <span className={cn('inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium', actionColors[log.action] || 'bg-gray-100 text-gray-700')}>
                            {log.action}
                          </span>
                        </td>
                        <td className="px-4 py-3 text-muted-foreground">
                          {log.entityName}{log.entityId ? ` #${log.entityId}` : ''}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div className="lg:col-span-1">
          {selectedLog ? (
            <div className="rounded-xl border border-border bg-card">
              <div className="px-4 py-3 border-b border-border">
                <h3 className="text-sm font-semibold text-foreground">Change Details</h3>
                <p className="text-xs text-muted-foreground mt-0.5">
                  {selectedLog.action} on {selectedLog.entityName} #{selectedLog.entityId}
                </p>
              </div>
              <div className="p-4 space-y-4 text-xs">
                <div>
                  <span className="font-medium text-muted-foreground uppercase tracking-wider">Old Value</span>
                  {selectedLog.oldData ? (
                    <pre className="mt-1 rounded-lg bg-muted p-3 overflow-x-auto text-foreground font-mono text-xs leading-relaxed">
                      {formatJson(selectedLog.oldData)}
                    </pre>
                  ) : (
                    <p className="mt-1 text-muted-foreground italic">None</p>
                  )}
                </div>
                <div>
                  <span className="font-medium text-muted-foreground uppercase tracking-wider">New Value</span>
                  {selectedLog.newData ? (
                    <pre className="mt-1 rounded-lg bg-muted p-3 overflow-x-auto text-foreground font-mono text-xs leading-relaxed">
                      {formatJson(selectedLog.newData)}
                    </pre>
                  ) : (
                    <p className="mt-1 text-muted-foreground italic">None</p>
                  )}
                </div>
              </div>
            </div>
          ) : (
            <div className="rounded-xl border border-border bg-card flex items-center justify-center min-h-[200px]">
              <p className="text-sm text-muted-foreground">Click a row to view change details</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
