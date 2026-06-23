import React, { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, ArrowUp, ArrowDown, GitFork } from 'lucide-react';
import toast from 'react-hot-toast';
import { workflowApi } from '@/features/workflow/api/workflow.api';
import type { Workflow, WorkflowRequest, WorkflowStep, WorkflowStepRequest } from '@/features/workflow/types/workflow.types';
import { Loader } from '@/components/ui/Loader';
import { Button } from '@/components/ui/Button';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { cn } from '@/lib/utils';

const roles = ['ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_INTERNAL_EMPLOYEE', 'ROLE_DISTRIBUTOR', 'ROLE_CHANNEL_PARTNER'];

interface StepFlowProps {
  steps: WorkflowStep[];
  onMoveUp: (index: number) => void;
  onMoveDown: (index: number) => void;
  onEdit: (step: WorkflowStep) => void;
  onDelete: (stepId: number) => void;
}

const StepFlow: React.FC<StepFlowProps> = ({ steps, onMoveUp, onMoveDown, onEdit, onDelete }) => {
  return (
    <div className="relative flex flex-col items-center py-4">
      {steps.map((step, idx) => (
        <React.Fragment key={step.id}>
          <div className="flex items-center gap-3 rounded-xl border-2 border-primary/20 bg-primary/5 px-5 py-3 min-w-[300px]">
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-xs font-bold text-primary-foreground">
              {step.stepOrder}
            </div>
            <div className="flex-1">
              <div className="flex items-center gap-2">
                <span className="text-sm font-semibold text-foreground">{step.label}</span>
                <span className="inline-flex items-center rounded-full bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300 px-2 py-0.5 text-xs font-medium">
                  {step.roleName.replace('ROLE_', '')}
                </span>
                {step.mandatory && (
                  <span className="inline-flex items-center rounded-full bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300 px-2 py-0.5 text-xs font-medium">Required</span>
                )}
              </div>
              {step.description && <p className="text-xs text-muted-foreground mt-0.5">{step.description}</p>}
            </div>
            <div className="flex items-center gap-0.5">
              <button onClick={() => onMoveUp(idx)} disabled={idx === 0}
                className="flex h-7 w-7 items-center justify-center rounded text-muted-foreground hover:bg-accent hover:text-accent-foreground disabled:opacity-30 disabled:cursor-not-allowed">
                <ArrowUp className="h-3.5 w-3.5" />
              </button>
              <button onClick={() => onMoveDown(idx)} disabled={idx === steps.length - 1}
                className="flex h-7 w-7 items-center justify-center rounded text-muted-foreground hover:bg-accent hover:text-accent-foreground disabled:opacity-30 disabled:cursor-not-allowed">
                <ArrowDown className="h-3.5 w-3.5" />
              </button>
              <button onClick={() => onEdit(step)}
                className="flex h-7 w-7 items-center justify-center rounded text-muted-foreground hover:bg-accent hover:text-accent-foreground">
                <Pencil className="h-3.5 w-3.5" />
              </button>
              <button onClick={() => onDelete(step.id)}
                className="flex h-7 w-7 items-center justify-center rounded text-muted-foreground hover:bg-destructive/10 hover:text-destructive">
                <Trash2 className="h-3.5 w-3.5" />
              </button>
            </div>
          </div>
          {idx < steps.length - 1 && (
            <div className="flex flex-col items-center py-1">
              <ArrowDown className="h-5 w-5 text-muted-foreground/40" />
            </div>
          )}
        </React.Fragment>
      ))}
      {steps.length === 0 && (
        <p className="text-sm text-muted-foreground py-6">No steps configured. Click + to add the first step.</p>
      )}
    </div>
  );
};

export const WorkflowListPage: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const canEdit = hasAnyRole('SUPER_ADMIN');
  const [workflows, setWorkflows] = useState<Workflow[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedWf, setSelectedWf] = useState<Workflow | null>(null);

  const [wfDrawer, setWfDrawer] = useState(false);
  const [wfForm, setWfForm] = useState<WorkflowRequest>({ name: '', description: '', module: '', active: true });
  const [editingWfId, setEditingWfId] = useState<number | null>(null);

  const [stepDrawer, setStepDrawer] = useState(false);
  const [stepForm, setStepForm] = useState<WorkflowStepRequest>({ workflowId: 0, stepOrder: 1, roleName: '', label: '', mandatory: true, slaHours: null, escalationRole: '', description: '' });
  const [editingStepId, setEditingStepId] = useState<number | null>(null);
  const [saving, setSaving] = useState(false);

  const fetchWorkflows = async () => {
    try {
      const res = await workflowApi.getAll();
      if (res.success) setWorkflows(res.data);
    } catch { toast.error('Failed to load workflows'); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchWorkflows(); }, []);

  const openWfCreate = () => {
    setWfForm({ name: '', description: '', module: '', active: true });
    setEditingWfId(null);
    setWfDrawer(true);
  };

  const openWfEdit = (wf: Workflow) => {
    setWfForm({ name: wf.name, description: wf.description, module: wf.module, active: wf.active });
    setEditingWfId(wf.id);
    setWfDrawer(true);
  };

  const handleWfSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!wfForm.name.trim()) { toast.error('Name is required'); return; }
    setSaving(true);
    try {
      if (editingWfId) {
        const res = await workflowApi.update(editingWfId, wfForm);
        if (res.success) { toast.success('Workflow updated'); setWfDrawer(false); fetchWorkflows(); }
      } else {
        const res = await workflowApi.create(wfForm);
        if (res.success) { toast.success('Workflow created'); setWfDrawer(false); fetchWorkflows(); }
      }
    } catch { toast.error(editingWfId ? 'Failed to update' : 'Failed to create'); }
    finally { setSaving(false); }
  };

  const handleDelete = async (wf: Workflow) => {
    if (!window.confirm(`Delete workflow "${wf.name}"?`)) return;
    try { const res = await workflowApi.delete(wf.id); if (res.success) { toast.success('Workflow deleted'); if (selectedWf?.id === wf.id) setSelectedWf(null); fetchWorkflows(); } }
    catch { toast.error('Failed to delete'); }
  };

  const openStepCreate = (wf: Workflow) => {
    const nextOrder = wf.steps.length > 0 ? Math.max(...wf.steps.map(s => s.stepOrder)) + 1 : 1;
    setStepForm({ workflowId: wf.id, stepOrder: nextOrder, roleName: '', label: '', mandatory: true, slaHours: null, escalationRole: '', description: '' });
    setEditingStepId(null);
    setStepDrawer(true);
  };

  const openStepEdit = (step: WorkflowStep) => {
    setStepForm({ workflowId: step.workflowId, stepOrder: step.stepOrder, roleName: step.roleName, label: step.label, mandatory: step.mandatory, slaHours: step.slaHours, escalationRole: step.escalationRole, description: step.description });
    setEditingStepId(step.id);
    setStepDrawer(true);
  };

  const handleStepSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!stepForm.label.trim()) { toast.error('Label is required'); return; }
    if (!stepForm.roleName) { toast.error('Role is required'); return; }
    setSaving(true);
    try {
      if (editingStepId) {
        const res = await workflowApi.updateStep(editingStepId, stepForm);
        if (res.success) { toast.success('Step updated'); setStepDrawer(false); fetchWorkflows(); }
      } else {
        const res = await workflowApi.addStep(stepForm);
        if (res.success) { toast.success('Step added'); setStepDrawer(false); fetchWorkflows(); }
      }
    } catch { toast.error(editingStepId ? 'Failed to update step' : 'Failed to add step'); }
    finally { setSaving(false); }
  };

  const handleDeleteStep = async (stepId: number) => {
    if (!window.confirm('Delete this step?')) return;
    try { const res = await workflowApi.deleteStep(stepId); if (res.success) { toast.success('Step deleted'); fetchWorkflows(); } }
    catch { toast.error('Failed to delete step'); }
  };

  const handleMoveUp = async (idx: number) => {
    if (!selectedWf || idx === 0) return;
    const steps = [...selectedWf.steps];
    const temp = steps[idx - 1].stepOrder;
    steps[idx - 1].stepOrder = steps[idx].stepOrder;
    steps[idx].stepOrder = temp;
    const a = steps[idx - 1], b = steps[idx];
    try {
      await workflowApi.updateStep(a.id, { workflowId: a.workflowId, stepOrder: a.stepOrder, roleName: a.roleName, label: a.label, mandatory: a.mandatory });
      await workflowApi.updateStep(b.id, { workflowId: b.workflowId, stepOrder: b.stepOrder, roleName: b.roleName, label: b.label, mandatory: b.mandatory });
      toast.success('Steps reordered');
      fetchWorkflows();
    } catch { toast.error('Failed to reorder'); }
  };

  const handleMoveDown = async (idx: number) => {
    if (!selectedWf || idx >= selectedWf.steps.length - 1) return;
    const steps = [...selectedWf.steps];
    const temp = steps[idx + 1].stepOrder;
    steps[idx + 1].stepOrder = steps[idx].stepOrder;
    steps[idx].stepOrder = temp;
    const a = steps[idx], b = steps[idx + 1];
    try {
      await workflowApi.updateStep(a.id, { workflowId: a.workflowId, stepOrder: a.stepOrder, roleName: a.roleName, label: a.label, mandatory: a.mandatory });
      await workflowApi.updateStep(b.id, { workflowId: b.workflowId, stepOrder: b.stepOrder, roleName: b.roleName, label: b.label, mandatory: b.mandatory });
      toast.success('Steps reordered');
      fetchWorkflows();
    } catch { toast.error('Failed to reorder'); }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-6xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Workflow Management</h1>
          <p className="text-sm text-muted-foreground mt-1">Configure approval chains with visual step flows</p>
        </div>
        {canEdit && (
          <Button onClick={openWfCreate} className="gap-2"><Plus className="h-4 w-4" /> New Workflow</Button>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1 space-y-2">
          <h2 className="text-sm font-semibold text-muted-foreground uppercase tracking-wider mb-3">Workflows</h2>
          {workflows.length === 0 ? (
            <div className="rounded-xl border border-border bg-card px-4 py-8 text-center text-sm text-muted-foreground">No workflows yet.</div>
          ) : (
            workflows.map(wf => (
              <div
                key={wf.id}
                onClick={() => setSelectedWf(wf)}
                className={cn(
                  'flex items-center gap-3 rounded-xl border px-4 py-3 cursor-pointer transition-all',
                  selectedWf?.id === wf.id
                    ? 'border-primary bg-primary/5 shadow-sm'
                    : 'border-border bg-card hover:bg-accent/30'
                )}
              >
                <GitFork className="h-4 w-4 shrink-0 text-muted-foreground" />
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-medium text-foreground truncate">{wf.name}</span>
                    <span className={cn(
                      'inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium',
                      wf.active ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300' : 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-400'
                    )}>{wf.active ? 'Active' : 'Inactive'}</span>
                  </div>
                  <p className="text-xs text-muted-foreground mt-0.5">{wf.steps.length} step{wf.steps.length !== 1 ? 's' : ''}</p>
                </div>
                {canEdit && (
                  <div className="flex gap-1" onClick={e => e.stopPropagation()}>
                    <button onClick={() => openWfEdit(wf)}
                      className="flex h-7 w-7 items-center justify-center rounded text-muted-foreground hover:bg-accent hover:text-accent-foreground">
                      <Pencil className="h-3.5 w-3.5" />
                    </button>
                    <button onClick={() => handleDelete(wf)}
                      className="flex h-7 w-7 items-center justify-center rounded text-muted-foreground hover:bg-destructive/10 hover:text-destructive">
                      <Trash2 className="h-3.5 w-3.5" />
                    </button>
                  </div>
                )}
              </div>
            ))
          )}
        </div>

        <div className="lg:col-span-2">
          {selectedWf ? (
            <div className="rounded-xl border border-border bg-card">
              <div className="flex items-center justify-between px-5 py-4 border-b border-border">
                <div>
                  <h2 className="text-lg font-semibold text-foreground">{selectedWf.name}</h2>
                  {selectedWf.description && <p className="text-sm text-muted-foreground">{selectedWf.description}</p>}
                </div>
                {canEdit && (
                  <Button size="sm" onClick={() => openStepCreate(selectedWf)} className="gap-1">
                    <Plus className="h-3.5 w-3.5" /> Add Step
                  </Button>
                )}
              </div>
              <StepFlow
                steps={selectedWf.steps}
                onMoveUp={handleMoveUp}
                onMoveDown={handleMoveDown}
                onEdit={openStepEdit}
                onDelete={handleDeleteStep}
              />
            </div>
          ) : (
            <div className="rounded-xl border border-border bg-card flex items-center justify-center min-h-[300px]">
              <p className="text-muted-foreground">Select a workflow to view its step flow</p>
            </div>
          )}
        </div>
      </div>

      {/* Workflow Drawer */}
      {wfDrawer && (
        <div className="fixed inset-0 z-50 flex justify-end">
          <div className="fixed inset-0 bg-black/40" onClick={() => setWfDrawer(false)} />
          <div className="relative w-full max-w-lg bg-background border-l border-border shadow-xl overflow-y-auto">
            <div className="flex items-center justify-between px-6 py-4 border-b border-border">
              <h2 className="text-lg font-semibold">{editingWfId ? 'Edit Workflow' : 'New Workflow'}</h2>
              <button onClick={() => setWfDrawer(false)} className="text-muted-foreground hover:text-foreground text-xl">&times;</button>
            </div>
            <form onSubmit={handleWfSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Name</label>
                <input type="text" value={wfForm.name} onChange={e => setWfForm({ ...wfForm, name: e.target.value })}
                  placeholder="e.g. ACCESS_REQUEST" className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Description</label>
                <textarea value={wfForm.description || ''} onChange={e => setWfForm({ ...wfForm, description: e.target.value })}
                  placeholder="What this workflow controls" rows={3} className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Module</label>
                <input type="text" value={wfForm.module || ''} onChange={e => setWfForm({ ...wfForm, module: e.target.value })}
                  placeholder="e.g. orders" className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
              </div>
              <label className="flex items-center gap-2">
                <input type="checkbox" checked={wfForm.active} onChange={e => setWfForm({ ...wfForm, active: e.target.checked })} className="rounded border-input" />
                <span className="text-sm">Active</span>
              </label>
              <div className="flex gap-3 pt-4">
                <Button type="submit" isLoading={saving} className="flex-1">{editingWfId ? 'Update' : 'Create'}</Button>
                <Button type="button" variant="outline" onClick={() => setWfDrawer(false)}>Cancel</Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Step Drawer */}
      {stepDrawer && (
        <div className="fixed inset-0 z-50 flex justify-end">
          <div className="fixed inset-0 bg-black/40" onClick={() => setStepDrawer(false)} />
          <div className="relative w-full max-w-lg bg-background border-l border-border shadow-xl overflow-y-auto">
            <div className="flex items-center justify-between px-6 py-4 border-b border-border">
              <h2 className="text-lg font-semibold">{editingStepId ? 'Edit Step' : 'Add Step'}</h2>
              <button onClick={() => setStepDrawer(false)} className="text-muted-foreground hover:text-foreground text-xl">&times;</button>
            </div>
            <form onSubmit={handleStepSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Label</label>
                <input type="text" value={stepForm.label} onChange={e => setStepForm({ ...stepForm, label: e.target.value })}
                  placeholder="e.g. Manager Review" className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Role</label>
                <select value={stepForm.roleName} onChange={e => setStepForm({ ...stepForm, roleName: e.target.value })}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring">
                  <option value="">Select role...</option>
                  {roles.map(r => <option key={r} value={r}>{r.replace('ROLE_', '')}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Order</label>
                <input type="number" value={stepForm.stepOrder} onChange={e => setStepForm({ ...stepForm, stepOrder: parseInt(e.target.value) || 1 })}
                  min={1} className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
              </div>
              <label className="flex items-center gap-2">
                <input type="checkbox" checked={stepForm.mandatory} onChange={e => setStepForm({ ...stepForm, mandatory: e.target.checked })} className="rounded border-input" />
                <span className="text-sm font-medium">Mandatory</span>
              </label>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">SLA (hours)</label>
                <input type="number" value={stepForm.slaHours || ''} onChange={e => setStepForm({ ...stepForm, slaHours: e.target.value ? parseInt(e.target.value) : null })}
                  min={1} className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Escalation Role</label>
                <select value={stepForm.escalationRole || ''} onChange={e => setStepForm({ ...stepForm, escalationRole: e.target.value || null })}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring">
                  <option value="">None</option>
                  {roles.map(r => <option key={r} value={r}>{r.replace('ROLE_', '')}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Description</label>
                <textarea value={stepForm.description || ''} onChange={e => setStepForm({ ...stepForm, description: e.target.value })}
                  rows={2} className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
              </div>
              <div className="flex gap-3 pt-4">
                <Button type="submit" isLoading={saving} className="flex-1">{editingStepId ? 'Update' : 'Add'}</Button>
                <Button type="button" variant="outline" onClick={() => setStepDrawer(false)}>Cancel</Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
