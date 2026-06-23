# Channel360 — Core Business Domain & Authorization Matrix

This document defines the rules managing user scopes, geographic scoping, permissions, role definitions, and operation pipelines.

---

## 1. Core Roles — Platform vs Business Separation

Channel360 strictly decouples **Platform Administration** from **Business Administration**.

### SUPER_ADMIN (Platform Owner)

**Responsibilities:** User Management, Role Management, Permission Management, Menu Management, CMS Management, Popup Management, Region Management, Approval Workflow Configuration, System Configuration.

**Can:** Create Users, Create Roles, Assign Permissions, Configure Menus, Configure Approval Chains, Configure Regions, Manage CMS.

**Cannot:** View Channel Data, View Claims, View Reports, Approve Transactions, Approve Claims, Approve Business Requests.

SUPER_ADMIN manages the platform but not the business.

---

### ADMIN (Business Administrator)

**Responsibilities:** Channel Operations, Business Approvals, Reports, Claims, Escalations.

**Can:** View All Data, View All Regions, Approve Any Request, Override Approval Chains, Grant Access Directly, Manage Business Operations.

ADMIN is the highest business authority.

---

### MANAGER (Region Scoped Approver)

**Responsibilities:** Review Requests, Approve Transactions, Manage Assigned Regions.

**Can:** View Assigned Region Data, Approve Requests Within Scope.

**Design Constraint:** Do NOT create separate roles per hierarchy level. There is only one `MANAGER` role. Authority is derived from assigned region:

```
Role = MANAGER, Region(State) = Karnataka       => State Manager
Role = MANAGER, Region = South India     => Regional Manager
Role = MANAGER, Region(Country) = India           => National Manager
```

---

### INTERNAL_EMPLOYEE

Company employee. Examples: Operations, Sales, Finance, Product Team.
Internal access level with company-wide operational tools.

---

### EXTERNAL_EMPLOYEE

Contractors and outsourced users. Limited operational access.
Cannot view internal company data outside their scope.

---

### DISTRIBUTOR

Distributor partner users. Automatically scoped to their company record.
Cannot view other companies' data.
Can upload daily sales, manage inventory within their scope.

---

### CHANNEL_PARTNER

Channel partner users. Automatically scoped to their company record.
Cannot view other companies' data.
Can manage customer sales and product activations.

---

### GUEST

Temporary access granted by ADMIN. Minimal dashboard view.
Default role assigned at registration.

---

## 2. Dynamic Authorization Architecture

### No Java Enums for Roles or Permissions

Roles and granular permissions (`users.create`, `claims.approve`) are managed using database relational lookup tables (`roles`, `permissions`, `role_permissions`). This allows modifications without software redeployments.

Technical transaction indicators (e.g., `Status.ACTIVE`, `RequestStatus.PENDING`) must use standard Java Enums.

### Database-Driven Access Control

Required tables:
- `roles` — Role definitions
- `permissions` — `module.action` permission definitions
- `role_permissions` — Role-to-permission mapping
- `user_roles` — User-to-role mapping
- `user_regions` — User-to-region assignment (PRIMARY/SECONDARY/TEMPORARY)
- `access_requests` — Pending access requests
- `user_access_grants` — Granted individual permissions

Access evaluation order:
1. **Role Permissions** — Base permissions inherited from assigned roles
2. **Region Assignment** — Geographic scope limiting data visibility
3. **User Access Grants** — Individual permission overrides/grants

Final access = Role Permissions ∩ Region Scope ∪ Access Grants

---

## 3. Geographic Hierarchy Trees

Geographies use a self-referencing hierarchy inside a single `regions` table.

**Levels:** Zone → Region → State → Territory

**Tree types:** B2B, B2C (separate trees)

**Key columns:** `id`, `name`, `parent_id`, `level`, `tree_type`, `path`

Do NOT create separate tables for each level (no `zones`, `states`, `territories` tables).

---

## 4. Approval Architecture

Approval chains are NOT hardcoded. They are stored in configurable database tables:

- `approval_workflows` — Workflow definitions (name, module, active)
- `approval_workflow_steps` — Step definitions (step_order, approver_role, level)

SUPER_ADMIN configures workflows dynamically. No code changes required.

### Example Workflows

| Workflow | Steps |
|----------|-------|
| Access Request | State Manager → Regional Manager → ADMIN |
| Batch Upload Exception | Regional Manager → ADMIN |
| Claims | State Manager → ADMIN |

---

## 5. Access Request System

- Users submit requests for specific permission + region scope
- Duplicate active requests are blocked until resolution
- Approval routing goes through workflow engine (chain of approvers)
- ADMIN can bypass the approval chain and grant directly
- Grants are permanent until explicitly revoked
- Full audit trail via `audit_logs`

---

## 6. Master Data Governance

| Master Data | Editable By |
|-------------|-------------|
| City Master | ADMIN |
| Product Master | ADMIN |
| Partner Master | ADMIN |
| Customer Master | ADMIN, Operations Team |

External users (DISTRIBUTOR, CHANNEL_PARTNER) cannot modify master data.

---

## 7. Reporting Architecture

Do NOT query transactional tables directly. Create:
- Reporting Views
- Materialized Views (for performance)
- Fact Tables (`fact_sales`, `fact_transfers`, `fact_activations`)

Design for millions of records.

---

## 8. Business Operation Pipeline

```
Channel Entry ----> Partner Transfer ----> Customer Purchase ----> Product Activation
(Mfg → Dist)        (Dist → Partner)       (Partner → Client)      (Warranty/Telemetry)
```

**Data Enrichment Engine:** Transactions (like Daily Sales CSV ingestion) auto-resolve text inputs against City Master data to assign geographic IDs and link rows to parent territories.

---

## 9. Audit Requirements

Mandatory from Day One. Every important action must be auditable:
- Who approved an upload?
- Who changed a city mapping?
- Who granted access?
- Who modified a product master record?

Audit via `audit_logs` table with JSONB diffs (`old_value`, `new_value`).

---

## 10. File Management

Do not store file URLs across multiple tables. Use a single `files` table with Supabase Storage.
All modules reference file IDs from the `files` table.
