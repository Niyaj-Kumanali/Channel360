# Existing System Analysis — Channel360 Approval & Workflow Engine

> **Context:** This document captures the complete state of the existing approval and workflow system before the new 22-phase implementation begins. All behaviors, edge cases, relationships, and business rules are documented here.

---

## 1. Two Coexisting Systems

The codebase contains **two generations** of approval/workflow systems that currently coexist:

### Legacy System (`approval` module)
- **Package:** `com.channel360.approval.*`
- **Tables:** `approval_workflows`, `approval_workflow_steps`, `approval_requests`, `approval_tasks`
- **Status enum:** `com.channel360.common.domain.ApprovalStatus` (PENDING, APPROVED, REJECTED)
- **Stored procedures:** `sp_workflow_save`, `sp_workflow_delete`, `sp_workflow_step_save`, `sp_workflow_step_delete`
- **Active controllers:** `ApprovalController` at `/api/v1/approval-requests`

### New Workflow Engine (`workflow` module)
- **Package:** `com.channel360.workflow.*`
- **Tables:** `workflows`, `workflow_versions`, `workflow_nodes`, `workflow_transitions`, `node_assignments`, `approver_rules`, `condition_expressions`, `workflow_requests`, `workflow_tasks`, `workflow_history`, `workflow_outbox`, `segments`, `business_processes`, `segment_process_mappings`
- **DAG-based graph model** with compiled workflow caching
- **Active controllers:** `WorkflowDesignerController`, `WorkflowRequestController`, `WorkflowTaskController`, `WorkflowLifecycleController`, `WorkflowSimulationController`
- **ADRs defined:** `0001` through `0009`

---

## 2. Table Relationships

### Legacy Approval Tables (V1)

```
approval_workflows
├── id (PK)
├── name, description, module, active
├── deleted_flag (soft delete)
│
└── approval_workflow_steps
    ├── id (PK)
    ├── workflow_id (FK → approval_workflows.id)
    ├── step_order (sequential)
    ├── role_name (string, e.g., "MANAGER", "ADMIN")
    ├── mandatory (boolean)
    ├── sla_hours (nullable)
    ├── escalation_role (nullable)
    ├── deleted_flag (soft delete)
    │
    └── approval_tasks (references via workflow_step_id)
        ├── id (PK)
        ├── approval_request_id (FK → approval_requests.id)
        ├── workflow_step_id (FK → approval_workflow_steps.id)
        ├── assigned_role_id
        ├── assigned_user_id (resolved via region hierarchy)
        ├── assigned_region_id
        ├── status: PENDING / APPROVED / REJECTED
        ├── approved_by, approved_at
        ├── rejected_by, rejected_at
        ├── comments
        └── @Version for optimistic locking

approval_requests
├── id (PK)
├── workflow_id (FK → approval_workflows.id)
├── request_type (e.g., "ACCESS_REQUEST")
├── request_reference_id (nullable)
├── request_region_id (nullable)
├── requestor_id (FK → users.id)
├── status: PENDING / APPROVED / REJECTED
└── @Version
```

### New Workflow Tables (V2)

```
workflows
├── id (PK)
├── name, description, active, metadata
└── no deleted_flag (no soft delete)

workflow_versions
├── id (PK)
├── workflow_id (FK → workflows.id)
├── version_number (incremental)
├── status: DRAFT / PUBLISHED / ARCHIVED
├── graph_json (jsonb - full graph snapshot)
├── @Version (entity_version)
│
└── workflow_nodes (for a given version)
    ├── id (PK)
    ├── node_uuid (UUID, UNIQUE)
    ├── workflow_version_id (FK → workflow_versions.id)
    ├── name, type (START/APPROVAL/TASK/GATEWAY/END)
    ├── terminal_type (nullable: SUCCESS/REJECTION/TERMINAL)
    ├── @Version (entity_version)
    │
    ├── node_assignments (1:1 via node_id)
    │   ├── id (PK)
    │   ├── assignment_uuid (UUID, UNIQUE)
    │   ├── node_id (FK → workflow_nodes.id, UNIQUE)
    │   ├── policy: ANY_ONE / FIRST_RESPONSE / ALL / MAJORITY / MINIMUM_APPROVALS / SEQUENTIAL / HIERARCHY
    │   ├── required_approval_count
    │   │
    │   └── approver_rules
    │       ├── id (PK)
    │       ├── rule_uuid (UUID, UNIQUE)
    │       ├── assignment_id (FK → node_assignments.id)
    │       ├── approver_type: ROLE / USER / REGIONAL_ROLE / DEPARTMENT_HEAD / HIERARCHY / DYNAMIC
    │       ├── role_name (nullable)
    │       ├── user_id (nullable)
    │       ├── region_id (nullable)
    │       ├── department (nullable)
    │       └── dynamic_provider (nullable)
    │
    ├── workflow_transitions
    │   ├── id (PK)
    │   ├── transition_uuid (UUID, UNIQUE)
    │   ├── source_node_id (FK → workflow_nodes.id)
    │   ├── target_node_id (FK → workflow_nodes.id)
    │   ├── action: SUBMIT / APPROVE / REJECT / SEND_BACK / DELEGATE / ESCALATE / CANCEL / TIMEOUT / AUTO
    │   ├── label, priority
    │   ├── @Version (entity_version)
    │   │
    │   └── condition_expressions (tree structure)
    │       ├── id (PK)
    │       ├── condition_uuid (UUID, UNIQUE)
    │       ├── transition_id (FK → workflow_transitions.id)
    │       ├── parent_id (self-FK for GROUP nesting)
    │       ├── type: GROUP / LEAF
    │       ├── operator: AND / OR (for GROUP)
    │       ├── field, op (eq/neq/gt/gte/lt/lte/contains/in/between), value

workflow_requests
├── id (PK)
├── workflow_version_id (FK → workflow_versions.id)
├── current_node_id (FK → workflow_nodes.id, tracks position)
├── request_type
├── request_reference_id
├── requestor_id
├── status: PENDING / IN_PROGRESS / APPROVED / REJECTED / CANCELLED / WITHDRAWN
├── metadata_json (jsonb)
├── idempotency_key (UNIQUE)
├── @Version (entity_version)
│
├── workflow_tasks
│   ├── id (PK)
│   ├── request_id (FK → workflow_requests.id)
│   ├── node_id (FK → workflow_nodes.id)
│   ├── assigned_user_id, assigned_role_id
│   ├── status: PENDING / IN_PROGRESS / APPROVED / REJECTED / SKIPPED / TIMED_OUT / DELEGATED
│   ├── acted_by, acted_at, comments
│   └── @Version (entity_version)
│
└── workflow_history
    ├── id (PK)
    ├── request_id (FK → workflow_requests.id)
    ├── from_node_id, to_node_id
    ├── action
    ├── actor_id, comments
    └── created_at (audit-only, no @Version)

workflow_outbox
├── id (PK)
├── aggregate_type, aggregate_id
├── event_type
├── payload (text)
├── status: PENDING / PUBLISHED / FAILED
├── retry_count, next_retry_at, last_error, published_at

segments
├── id (PK)
├── name (UNIQUE), code (UNIQUE), description, active

business_processes
├── id (PK)
├── name (UNIQUE), code (UNIQUE), description, active

segment_process_mappings
├── id (PK)
├── segment_id (FK → segments.id)
├── business_process_id (FK → business_processes.id)
└── UNIQUE(segment_id, business_process_id)
```

### Supporting Tables

```
users
├── id (PK), email (UNIQUE), first_name, last_name
├── mobile_number, gender, address, date_of_birth, profile_image_url
├── is_active

roles
├── id (PK), name (UNIQUE), description

regions (self-referencing hierarchy)
├── id (PK), name, type, code
├── parent_id (self-FK), is_active

region_approvers (V1 only - maps users to region+role)
├── id (PK)
├── region_id (FK → regions.id)
├── user_id (FK → users.id)
├── level (1-based hierarchy depth)
└── is_active

audit_logs
├── id (PK)
├── user_id, action, module_name, entity_name, entity_id
├── old_data (jsonb), new_data (jsonb)
└── created_at
```

---

## 3. Approval Status Lifecycle

### Legacy V1 Status Flow
```
PENDING ──→ APPROVED (all tasks approved)
PENDING ──→ REJECTED (any task rejected or explicit reject)

ApprovalTask: PENDING → APPROVED | REJECTED
Once all tasks are resolved, the ApprovalRequest status is updated.
If ANY task is REJECTED → entire request → REJECTED
If ALL tasks are APPROVED → entire request → APPROVED (+ event published)
```

### New V2 Status Flow
```
             ┌─────────────────────────────┐
             │        PENDING              │
             │  (initial submission)        │
             └──────────┬──────────────────┘
                        │
                        v
             ┌─────────────────────────────┐
             │       IN_PROGRESS           │
             │  (flowing through nodes)     │
             └──┬──────────┬───────────────┘
                │          │
                v          v
    ┌───────────────┐  ┌───────────────┐
    │   APPROVED    │  │   REJECTED    │
    │  (terminal)   │  │  (terminal)   │
    └───────────────┘  └───────────────┘

Also: CANCELLED, WITHDRAWN (terminal states)

WorkflowTask status:
PENDING → IN_PROGRESS | APPROVED | REJECTED | SKIPPED | TIMED_OUT | DELEGATED
```

---

## 4. Approval Hierarchy & Role Resolution

### V1 Resolution (in `ApprovalService.resolveApprover`)
1. Build region ancestry chain: `regionId → parent → grandparent → ... → root`
2. For each region in the chain (starting with the request's region):
   - Query `region_approvers` for `(region_id, role_id)` match
   - Return first matching `user_id`
3. If no approver found in chain → `null` (task has no assigned user)

### V2 Resolution (in `TransitionService.resolveApprovers`)
1. Assignment rules are resolved from the `BusinessContext` via key `assignmentRules_<nodeId>`
2. Each `GraphApproverRule` is dispatched to a typed `ApproverResolver`:
   - `RoleApproverResolver` → Looks up `userIdByRole_<roleName>` from context
   - `UserApproverResolver` → Returns fixed `rule.userId()`
   - `RegionalRoleApproverResolver` → Looks up `regionApprovers_<regionId>_<roleName>` from context
   - `HierarchyResolver` → Looks up `managerOf_<requestorId>` from context
   - `DepartmentHeadResolver` → Looks up `departmentHead_<department>` from context
   - `DynamicResolver` → Looks up `dynamic_<dynamicProvider>` from context

### Region Hierarchy Building (V1, `buildRegionChain`)
- Walk parent chain via `RegionFacade.getById()` repeatedly
- Returns list: `[regionId, parentId, grandparentId, ...]`

---

## 5. Stored Procedures

### Workflow Procedures (V1 Legacy)
| Procedure | Purpose |
|-----------|---------|
| `sp_workflow_save` | Insert/update approval_workflows |
| `sp_workflow_delete` | Soft-delete (set deleted_flag) |
| `sp_workflow_step_save` | Insert/update approval_workflow_steps |
| `sp_workflow_step_delete` | Soft-delete step |

### Region Approver Procedures (V1)
| Procedure | Purpose |
|-----------|---------|
| `sp_region_approver_save` | Insert/update region_approvers |
| `sp_region_approver_deactivate` | Soft-deactivate |

### Audit Procedure
| Procedure | Purpose |
|-----------|---------|
| `sp_audit_log_insert` | Function returning BIGINT, inserts audit_logs row |

### Auth Procedures
| Procedure | Purpose |
|-----------|---------|
| `sp_auth_users_save` | Insert/update auth_users |
| `sp_auth_update_last_login` | Update last_login timestamp |
| `sp_auth_change_password` | Update password |
| `sp_refresh_tokens_save` | Insert/update refresh tokens |
| `sp_refresh_tokens_revoke` | Revoke token |
| `sp_refresh_tokens_delete_by_user_id` | Delete all tokens for user |

### User Procedures
| Procedure | Purpose |
|-----------|---------|
| `sp_users_save` | Insert/update users |
| `sp_users_delete` | Cascade delete user + roles + auth + tokens |
| `sp_users_assign_roles` | Replace all role assignments for a user |
| `sp_users_list` | Paginated, filtered user list (REFCURSOR) |
| `sp_users_count` | Count users matching filter |
| `sp_auth_change_password` | Duplicate in users.sql |

### Region Procedures
| Procedure | Purpose |
|-----------|---------|
| `sp_regions_save` | Insert/update with materialized path |
| `sp_regions_delete` | Soft-delete |

### Roles Procedures
| Procedure | Purpose |
|-----------|---------|
| `sp_roles_save` | Insert/update roles |
| `sp_roles_delete` | Delete + cascade role_permissions, user_roles |
| `sp_roles_list` | List all roles (REFCURSOR) |

---

## 6. Domain Events

### Legacy V1 Events
| Event | Publisher | Listener Side Effect |
|-------|-----------|---------------------|
| `WorkflowCreatedEvent` | `WorkflowService.createWorkflow()` | Logged by `DomainEventListener` |
| `WorkflowApprovedEvent` | `ApprovalService.checkAndUpdateRequestStatus()` | Logged by `DomainEventListener` (AFTER_COMMIT) |

### New V2 Events (sealed interface `WorkflowEvent`)
| Event | Fields | Published Via |
|-------|--------|--------------|
| `TaskCreatedEvent` | requestId, taskId, aggregateId, assignedUserId, occurredAt | `WorkflowEventPublisher` → Outbox |
| `TaskAssignedEvent` | requestId, taskId, aggregateId, assignedUserId, occurredAt | Outbox |
| `TaskApprovedEvent` | requestId, taskId, aggregateId, approvedBy, comments, occurredAt | Outbox |
| `TaskRejectedEvent` | requestId, taskId, aggregateId, rejectedBy, comments, occurredAt | Outbox |
| `TaskDelegatedEvent` | requestId, taskId, aggregateId, delegatedTo, delegatedBy, occurredAt | Outbox |
| `TaskEscalatedEvent` | requestId, taskId, aggregateId, escalatedTo, occurredAt | Outbox |
| `TaskTimedOutEvent` | requestId, taskId, aggregateId, occurredAt | Outbox |
| `TaskSendBackEvent` | requestId, taskId, aggregateId, sentBy, occurredAt | Outbox |
| `NodeCompletedEvent` | requestId, nodeId, aggregateId, occurredAt | Outbox |
| `WorkflowStartedEvent` | requestId, aggregateId, workflowVersionId, requestorId, occurredAt | Outbox |
| `WorkflowCompletedEvent` | requestId, aggregateId, occurredAt | Outbox |
| `WorkflowCancelledEvent` | requestId, aggregateId, cancelledBy, occurredAt | Outbox |
| `WorkflowRejectedEvent` | requestId, aggregateId, rejectedBy, occurredAt | Outbox |

All V2 events go through `WorkflowEventPublisher` → `OutboxService.saveEvent()` → `workflow_outbox` table.

**Note:** The `WorkflowApprovedEvent` (V1 event) is different from the `WorkflowApprovedEvent` in V2 — they have different packages. V1 events go directly to `DomainEventListener` via Spring `ApplicationEventPublisher`, while V2 events go via the outbox pattern.

---

## 7. Existing Action Handlers (V2 Engine)

| Handler | Action | Behavior |
|---------|--------|----------|
| `SubmitHandler` | SUBMIT | Resolve transition from START node, advance to target |
| `ApproveHandler` | APPROVE | Resolve transition from current node, advance |
| `RejectHandler` | REJECT | Resolve transition, if target is terminal → rejection result |
| `SendBackHandler` | SEND_BACK | Resolve transition to previous node |
| `DelegateHandler` | DELEGATE | Resolve transition to delegate target |
| `CancelHandler` | CANCEL | Terminal — returns `ExecutionResult.cancelled()` |
| `EscalateHandler` | ESCALATE | Resolve transition to escalation target |
| `TimeoutHandler` | TIMEOUT | (empty handler registered for TIMEOUT) |

---

## 8. Existing Validation Rules (V2)

23 rules across 7 phases (as per ADR-0008):

### STRUCTURE Phase
1. Exactly one START node
2. At least one terminal node

### REFERENCES Phase
3. All node references in transitions exist
4. No broken transitions (orphan targets)

### GRAPH Phase
5. No cycles
6. All nodes reachable from START
7. No orphan nodes
8. Infinite loop detection

### TRANSITIONS Phase
9. No duplicate transitions
10. Duplicate action rule
11. Transition gap rule
12. Transition overlap rule
13. Deterministic transition rule
14. No duplicate transition labels
15. Terminal node no outgoing transitions
16. Node with no outgoing transition

### ASSIGNMENTS Phase
17. Every approval node has assignment
18. Circular assignment rule
19. Unpublished approver roles

### CONDITIONS Phase
20. Unused condition rule

### PUBLISH Phase
21. Valid transition actions
22. Workflow without approval node
23. Unused node

---

## 9. BusinessContext & BusinessField

### Current Implementation
- `BusinessContext` wraps a `Map<String, Object>` with `Map.copyOf(values)` for immutability
- Provides `get(BusinessField<T>)` for typed access and `getRaw(String)` for untyped access
- `BusinessField<T>` is a record: `(String name, Class<T> type)`
- Business field groups exist but are not yet actually typed — they return `BusinessField<String>` everywhere via static methods

### BusinessField Groups
| Class | Fields |
|-------|--------|
| `WorkflowBusinessFields` | workflowId, requestType, requestorId, regionId, department, amount, priority, comment, initiator |
| `SalesBusinessFields` | salesAmount, region, productCategory, distributorId |
| `FinanceBusinessFields` | invoiceAmount, budgetCode, costCenter, currency |
| `HRBusinessFields` | employeeId, department, position, tenureYears |

Note: These are not yet enforced by the compiler. The `ConditionCompiler` uses raw `ctx.getRaw()` calls.

---

## 10. Compilation Pipeline (V2)

```
WorkflowGraph (from JSON)
    │
    ▼
ConditionCompiler.compile()
    │
    ▼
CompiledWorkflow
    ├── workflowVersionId (nullable, set by CachedWorkflowDefinitionProvider)
    ├── startNodeId (UUID)
    ├── nodes: Map<UUID, NodeRef>
    └── outgoingTransitions: Map<UUID, List<CompiledTransition>>
         └── each CompiledTransition has a CompiledCondition (Predicate<BusinessContext>)
```

**CachedWorkflowDefinitionProvider:**
- Warmed on startup: loads all PUBLISHED versions
- Lazy compiles on cache miss
- Evict on version status change

---

## 11. Execution Pipeline (V2)

```
NormalExecutionService.startWorkflow()
1. Idempotency check
2. Load CompiledWorkflow via WorkflowDefinitionProvider
3. WorkflowEngine.startWorkflow() (no DB, no events)
4. RequestService.createRequest() → Save to DB
5. ExecutionApplier.apply() → Persist AuditPlan + TaskPlan

NormalExecutionService.executeAction()
1. Idempotency check
2. Load task + request + compiled workflow
3. Determine current node
4. TaskService.markActedOn() → Save task status
5. WorkflowEngine.execute() (no DB)
6. ExecutionApplier.apply() → Persist results
```

---

## 12. Special Cases & Edge Cases

### Delegation
- `DelegateHandler` resolves a DELEGATE transition
- `NormalExecutionService.executeAction` marks task as `DELEGATED`
- Outbox publishes `TaskDelegatedEvent`

### Send Back
- `SendBackHandler` resolves a SEND_BACK transition to a previous node
- Task marked as `PENDING` (reset) on send back
- Outbox publishes `TaskSendBackEvent`

### Escalation
- `EscalationScheduler` runs every 60 seconds
- Finds all PENDING tasks
- Calls `WorkflowEngine.execute()` with `TransitionAction.ESCALATE`
- Outbox publishes `TaskEscalatedEvent`

### Timeout
- `TimeoutHandler` is registered but has empty handle (returns null behavior)
- `TransitionAction.TIMEOUT` exists in enum
- `TaskTimedOutEvent` exists but may not be triggered automatically yet

### Cancellation
- `CancelHandler` returns terminal `ExecutionResult.cancelled()`
- `WorkflowCancelledEvent` published via outbox
- All existing tasks get status CANCELLED

### Double Approval Prevention
- `TaskAlreadyActedOnException` thrown if `task.getStatus() != PENDING`
- `@Version` optimistic locking prevents concurrent task actions

### Idempotency
- `IdempotencyService` checks in-memory `ConcurrentHashMap` first, then DB lookup
- `idempotency_key` column is UNIQUE in `workflow_requests` table
- Race condition: in-memory cache may miss on concurrent requests with same key (first request wins via DB unique constraint)

### Thread Safety Concerns
- `IdempotencyService` uses `ConcurrentHashMap` but is not distributed-safe
- `CachedWorkflowDefinitionProvider` cache is local JVM cache
- `EscalationScheduler` has no locking — could double-escalate tasks if multiple instances

---

## 13. Notification Triggers

Currently **no explicit notification sending** is implemented. The `DomainEventListener` only logs events:

- `UserCreatedEvent` → log
- `UserUpdatedEvent` → log
- `RoleAssignedEvent` → log
- `RoleCreatedEvent` → log
- `RoleUpdatedEvent` → log
- `RoleDeletedEvent` → log
- `WorkflowCreatedEvent` → log
- `WorkflowApprovedEvent` → log (AFTER_COMMIT)

There is an `EmailService` bean injected but it is not called for any workflow/approval events.

---

## 14. Audit/History Behavior

**Legacy V1:** No workflow-specific audit. Separate `audit_logs` table with JSONB diffs.

**New V2:**
- `workflow_history` table records every action (from→to node, action, actor, comments)
- Written by `ExecutionApplier.saveHistory()` during `ExecutionApplier.apply()`
- `AuditPlan` from `ExecutionResult.audit()` drives what gets recorded

The `audit_logs` table continues to exist for general audit (outside the workflow engine).

---

## 15. Hardcoded Business Rules

### V1 Hardcoded Behaviors
1. **Sequential linear approval:** Steps are ordered by `step_order` and must be approved sequentially
2. **Any rejection → full rejection:** `checkAndUpdateRequestStatus()` in `ApprovalService`
3. **All approved → event:** Only when ALL tasks are APPROVED, publish `WorkflowApprovedEvent`
4. **Region chain walk:** Always walk from specific → parent → grandparent → root
5. **Role name as string:** `step.roleName()` is a string, resolved via `RoleFacade.findByName()`
6. **No delegation, send-back, escalation in V1:** None of these are implemented in the legacy system

### V2 Hardcoded Behaviors
1. **Single start node:** `WorkflowEngine.startWorkflow()` always uses `TransitionAction.SUBMIT`
2. **First assignee wins:** `TaskService.createTask()` only creates a task for the first resolved assignee
3. **Approval policy not enforced:** AssignmentPolicy is stored but only `ANY_ONE` is effectively used
4. **SEND_BACK reset:** Task is reset to `PENDING` (not a new task)
5. **Simulator only uses SUBMIT/APPROVE:** Action choice is hardcoded, no branching paths explored
6. **Context-based resolution:** All resolvers read from BusinessContext (no DB lookups during rule resolution)

---

## 16. Security & Authorization

- `ApprovalController` uses `@RequirePermission("workflows.view")` and `@RequirePermission("workflows.configure")`
- V2 controllers (`WorkflowDesignerController`, `WorkflowRequestController`, `WorkflowTaskController`) do NOT have permission annotations
- Roles and permissions are DB-driven (not enums), per ADR-002

---

## 17. What Already Exists (Partial Phase Progress)

The V2 system has already implemented significant portions of the proposed 22-phase plan:

| Phase | What Exists | Status |
|-------|-------------|--------|
| 1 - Foundation Domain | Enums, value objects, models, exceptions | ✅ Done |
| 2 - Graph Model | WorkflowGraph, GraphNode, GraphTransition, GraphAssignment, GraphApproverRule, GraphConditionExpression | ✅ Done |
| 3 - Compiled Workflow | CompiledWorkflow, CompiledTransition, CompiledCondition, WorkflowDefinitionProvider interface | ✅ Done |
| 4 - Persistence Model | All JPA entities | ✅ Done |
| 5 - Repository Layer | All Spring Data repositories | ✅ Done |
| 6 - DTO Layer | DTOs for designer, runtime, lifecycle, simulation, configuration | ✅ Done |
| 7 - Graph Synchronization | GraphConsistencyValidator, sync pipeline in DesignerService | ✅ Done |
| 8 - Validation Engine | ValidationRule, WorkflowValidator, 23 rules across 7 phases | ✅ Done |
| 9 - Compiler | ConditionCompiler, expression compiler | ✅ Done |
| 10 - Engine Plugins | 7 operators, FieldConditionEvaluator, 6 resolvers | ✅ Done |
| 11 - Workflow Engine | WorkflowEngine, TransitionService, 8 action handlers | ✅ Done |
| 12 - Workflow Definition Provider | CachedWorkflowDefinitionProvider with warm + lazy compile + evict | ✅ Done |
| 13 - Runtime | NormalExecutionService, ExecutionApplier, RequestService, TaskService, IdempotencyService | ✅ Done |
| 14 - Designer | WorkflowDesignerService, designer controller | ✅ Done |
| 15 - Lifecycle | WorkflowLifecycleService (publish, archive, draft) | ✅ Done (missing clone, activate, deactivate) |
| 16 - Simulation | WorkflowSimulator with execution tracing | ✅ Done |
| 17 - Events | 13 domain events (sealed interface), WorkflowEventPublisher | ✅ Done |
| 18 - Outbox | OutboxEvent entity, OutboxService, OutboxPoller | ✅ Done (missing FOR UPDATE SKIP LOCKED) |
| 19 - Scheduling | EscalationScheduler (60s cron) | ✅ Done (missing timeout, reminder) |
| 20 - Configuration | Segment, BusinessProcess entities and services | ✅ Done (missing controllers) |
| 21 - Security & API | Some controllers exist (designer, lifecycle, request, task, simulation) | ⚠️ Partial |
| 22 - Production Hardening | No tests yet, 9 ADRs exist | ⚠️ Partial |

---

## 18. Key Gaps in Existing V2 Implementation

1. **No tests exist** — Zero test files found in `src/test`
2. **Outbox poller** does not use `FOR UPDATE SKIP LOCKED` — uses basic `findPendingEvents` with limit
3. **No clone/activate/deactivate** lifecycle methods
4. **No timeout handler logic** — `TimeoutHandler.handle()` is empty
5. **Segments/BusinessProcess controllers** don't exist yet
6. **Permission annotations** missing on V2 controllers
7. **No notification sending** — only logging
8. **IdempotencyService** is JVM-local, not distributed
9. **AssignmentPolicy** not enforced in task creation (only first assignee used)
10. **V1 (`approval` module) still active** — both systems coexist
