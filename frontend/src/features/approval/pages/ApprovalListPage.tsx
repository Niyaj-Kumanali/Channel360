import React, { useEffect, useState } from 'react';
import { Plus, CheckCircle, XCircle, Clock, ChevronDown } from 'lucide-react';
import toast from 'react-hot-toast';
import { approvalApi } from '@/features/approval/api/approval.api';
import { workflowApi } from '@/features/workflow/api/workflow.api';
import { regionApi } from '@/features/region/api/region.api';
import type { ApprovalRequestResponse, ApprovalRequestCreate } from '@/features/approval/types/approval.types';
import type { Workflow } from '@/features/workflow/types/workflow.types';
import type { Region } from '@/features/region/types/region.types';
import { Loader } from '@/components/ui/Loader';
import { Button } from '@/components/ui/Button';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { cn } from '@/lib/utils';

const statusColors: Record<string, string> = {
  PENDING: 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300',
  APPROVED: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
  REJECTED: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
};

const StatusIcon: React.FC<{ status: string }> = ({ status }) => {
  if (status === 'APPROVED') return <CheckCircle className="h-5 w-5 text-green-600" />;
  if (status === 'REJECTED') return <XCircle className="h-5 w-5 text-red-600" />;
  return <Clock className="h-5 w-5 text-amber-500" />;
};

export const ApprovalListPage: React.FC = () => {
  const { user } = useAuth();
  const canEdit = true;

  const [requests, setRequests] = useState<ApprovalRequestResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [selected, setSelected] = useState<ApprovalRequestResponse | null>(null);

  const [drawerOpen, setDrawerOpen] = useState(false);
  const [workflows, setWorkflows] = useState<Workflow[]>([]);
  const [regions, setRegions] = useState<Region[]>([]);
  const [form, setForm] = useState<ApprovalRequestCreate>({
    workflowId: 0,
    requestType: '',
    requestRegionId: 0,
    requestorId: user?.id || 0,
  });
  const [saving, setSaving] = useState(false);
  const [actionTaskId, setActionTaskId] = useState<number | null>(null);
  const [actionComment, setActionComment] = useState('');

  const fetchRequests = async () => {
    try {
      const res = await approvalApi.getAll();
      if (res.success) setRequests(res.data);
    } catch { toast.error('Failed to load approval requests'); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchRequests(); }, []);

  const openCreate = async () => {
    try {
      const [wfRes, regRes] = await Promise.all([workflowApi.getAll(), regionApi.getAll()]);
      if (wfRes.success) setWorkflows(wfRes.data.filter(w => w.active));
      if (regRes.success) setRegions(regRes.data);
    } catch { toast.error('Failed to load form data'); }
    setForm({ workflowId: 0, requestType: '', requestRegionId: 0, requestorId: user?.id || 0 });
    setDrawerOpen(true);
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.workflowId || !form.requestType || !form.requestRegionId) {
      toast.error('All fields are required');
      return;
    }
    setSaving(true);
    try {
      const res = await approvalApi.create(form);
      if (res.success) {
        toast.success('Approval request created');
        setDrawerOpen(false);
        fetchRequests();
      }
    } catch { toast.error('Failed to create request'); }
    finally { setSaving(false); }
  };

  const handleApprove = async (taskId: number) => {
    try {
      const res = await approvalApi.approveTask(taskId, {
        userId: user?.id || 0,
        comments: actionTaskId === taskId ? actionComment : undefined,
      });
      if (res.success) {
        toast.success('Task approved');
        setActionTaskId(null);
        setActionComment('');
        fetchRequests();
      }
    } catch { toast.error('Failed to approve'); }
  };

  const handleReject = async (taskId: number) => {
    try {
      const res = await approvalApi.rejectTask(taskId, {
        userId: user?.id || 0,
        comments: actionTaskId === taskId ? actionComment : undefined,
      });
      if (res.success) {
        toast.success('Task rejected');
        setActionTaskId(null);
        setActionComment('');
        fetchRequests();
      }
    } catch { toast.error('Failed to reject'); }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-6xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Approval Requests</h1>
          <p className="text-sm text-muted-foreground mt-1">Submit and manage approval workflows with region-aware routing</p>
        </div>
        {canEdit && (
          <Button onClick={openCreate} className="gap-2"><Plus className="h-4 w-4" /> New Request</Button>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1 space-y-2">
          <h2 className="text-sm font-semibold text-muted-foreground uppercase tracking-wider mb-3">Requests</h2>
          {requests.length === 0 ? (
            <div className="rounded-xl border border-border bg-card px-4 py-8 text-center text-sm text-muted-foreground">No requests yet.</div>
          ) : (
            requests.map(req => (
              <div
                key={req.id}
                onClick={() => setSelected(selected?.id === req.id ? null : req)}
                className={cn(
                  'rounded-xl border px-4 py-3 cursor-pointer transition-all',
                  selected?.id === req.id
                    ? 'border-primary bg-primary/5 shadow-sm'
                    : 'border-border bg-card hover:bg-accent/30'
                )}
              >
                <div className="flex items-center justify-between mb-1">
                  <span className="text-sm font-medium text-foreground">{req.workflowName || `WF #${req.workflowId}`}</span>
                  <span className={cn('inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium', statusColors[req.status] || 'bg-gray-100 text-gray-600')}>
                    {req.status}
                  </span>
                </div>
                <p className="text-xs text-muted-foreground">
                  {req.requestType}{req.requestRegionName ? ` — ${req.requestRegionName}` : ''}
                </p>
                <p className="text-xs text-muted-foreground mt-1">{new Date(req.createdAt).toLocaleDateString()}</p>
              </div>
            ))
          )}
        </div>

        <div className="lg:col-span-2">
          {selected ? (
            <div className="rounded-xl border border-border bg-card">
              <div className="px-5 py-4 border-b border-border">
                <div className="flex items-center justify-between">
                  <div>
                    <h2 className="text-lg font-semibold text-foreground">{selected.workflowName || `Workflow #${selected.workflowId}`}</h2>
                    <p className="text-sm text-muted-foreground mt-0.5">
                      {selected.requestType}
                      {selected.requestRegionName ? ` — Region: ${selected.requestRegionName}` : ''}
                      {selected.requestorName ? ` — Requestor: ${selected.requestorName}` : ''}
                    </p>
                  </div>
                  <span className={cn('inline-flex items-center rounded-full px-3 py-1 text-sm font-medium', statusColors[selected.status])}>
                    {selected.status}
                  </span>
                </div>
              </div>

              <div className="p-5">
                <h3 className="text-sm font-semibold text-muted-foreground uppercase tracking-wider mb-4">Approval Timeline</h3>
                {selected.tasks.length === 0 ? (
                  <p className="text-sm text-muted-foreground text-center py-6">No tasks configured for this workflow.</p>
                ) : (
                  <div className="relative">
                    {selected.tasks.map((task, idx) => (
                      <div key={task.id} className="relative flex gap-4 pb-8 last:pb-0">
                        {idx < selected.tasks.length - 1 && (
                          <div className="absolute left-[11px] top-8 bottom-0 w-0.5 bg-border" />
                        )}
                        <div className="flex flex-col items-center shrink-0">
                          <div className={cn(
                            'flex h-6 w-6 items-center justify-center rounded-full',
                            task.status === 'APPROVED' ? 'bg-green-100' : task.status === 'REJECTED' ? 'bg-red-100' : 'bg-amber-100'
                          )}>
                            <StatusIcon status={task.status} />
                          </div>
                        </div>
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center gap-2 flex-wrap">
                            <span className="text-sm font-semibold text-foreground">{task.stepLabel || `Step ${task.stepOrder || ''}`}</span>
                            <span className="inline-flex items-center rounded-full bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300 px-2 py-0.5 text-xs font-medium">
                              {task.assignedRoleName?.replace('ROLE_', '') || `Role #${task.assignedRoleId}`}
                            </span>
                            {task.assignedUserName && (
                              <span className="text-xs text-muted-foreground">→ {task.assignedUserName}</span>
                            )}
                            {task.assignedRegionName && (
                              <span className="text-xs text-muted-foreground">({task.assignedRegionName})</span>
                            )}
                          </div>

                          {task.status === 'APPROVED' && task.approvedByName && (
                            <p className="text-xs text-green-600 mt-1">
                              Approved by {task.approvedByName}{task.approvedAt ? ` on ${new Date(task.approvedAt).toLocaleString()}` : ''}
                            </p>
                          )}
                          {task.status === 'REJECTED' && task.rejectedByName && (
                            <p className="text-xs text-red-600 mt-1">
                              Rejected by {task.rejectedByName}{task.rejectedAt ? ` on ${new Date(task.rejectedAt).toLocaleString()}` : ''}
                            </p>
                          )}
                          {task.comments && (
                            <p className="text-xs text-muted-foreground mt-1 italic">"{task.comments}"</p>
                          )}

                          {task.status === 'PENDING' && (
                            <div className="mt-2 space-y-2">
                              {actionTaskId === task.id && (
                                <textarea
                                  value={actionComment}
                                  onChange={e => setActionComment(e.target.value)}
                                  placeholder="Add a comment (optional)..."
                                  rows={2}
                                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-xs outline-none focus:border-ring focus:ring-1 focus:ring-ring"
                                />
                              )}
                              <div className="flex gap-2">
                                <button
                                  onClick={() => {
                                    if (actionTaskId === task.id) { handleApprove(task.id); }
                                    else { setActionTaskId(task.id); setActionComment(''); }
                                  }}
                                  className="inline-flex items-center gap-1 rounded-lg bg-green-600 px-3 py-1.5 text-xs font-medium text-white hover:bg-green-700 transition-colors"
                                >
                                  <CheckCircle className="h-3.5 w-3.5" /> Approve
                                </button>
                                <button
                                  onClick={() => {
                                    if (actionTaskId === task.id) { handleReject(task.id); }
                                    else { setActionTaskId(task.id); setActionComment(''); }
                                  }}
                                  className="inline-flex items-center gap-1 rounded-lg bg-red-600 px-3 py-1.5 text-xs font-medium text-white hover:bg-red-700 transition-colors"
                                >
                                  <XCircle className="h-3.5 w-3.5" /> Reject
                                </button>
                                {actionTaskId === task.id && (
                                  <button
                                    onClick={() => { setActionTaskId(null); setActionComment(''); }}
                                    className="text-xs text-muted-foreground hover:text-foreground px-2"
                                  >
                                    Cancel
                                  </button>
                                )}
                              </div>
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="rounded-xl border border-border bg-card flex items-center justify-center min-h-[300px]">
              <p className="text-muted-foreground">Select a request to view its approval timeline</p>
            </div>
          )}
        </div>
      </div>

      {drawerOpen && (
        <div className="fixed inset-0 z-50 flex justify-end">
          <div className="fixed inset-0 bg-black/40" onClick={() => setDrawerOpen(false)} />
          <div className="relative w-full max-w-lg bg-background border-l border-border shadow-xl overflow-y-auto">
            <div className="flex items-center justify-between px-6 py-4 border-b border-border">
              <h2 className="text-lg font-semibold text-foreground">New Approval Request</h2>
              <button onClick={() => setDrawerOpen(false)} className="text-muted-foreground hover:text-foreground text-xl">&times;</button>
            </div>
            <form onSubmit={handleCreate} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Workflow</label>
                <select value={form.workflowId} onChange={e => setForm({ ...form, workflowId: parseInt(e.target.value) || 0 })}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring">
                  <option value={0}>Select workflow...</option>
                  {workflows.map(w => <option key={w.id} value={w.id}>{w.name} ({w.steps.length} steps)</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Request Type</label>
                <input type="text" value={form.requestType} onChange={e => setForm({ ...form, requestType: e.target.value })}
                  placeholder="e.g. ACCESS_REQUEST, CLAIM_APPROVAL"
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Region</label>
                <select value={form.requestRegionId} onChange={e => setForm({ ...form, requestRegionId: parseInt(e.target.value) || 0 })}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring">
                  <option value={0}>Select region...</option>
                  {regions.map(r => <option key={r.id} value={r.id}>{r.path}</option>)}
                </select>
              </div>
              <div className="flex gap-3 pt-4">
                <Button type="submit" isLoading={saving} className="flex-1">Submit</Button>
                <Button type="button" variant="outline" onClick={() => setDrawerOpen(false)}>Cancel</Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
