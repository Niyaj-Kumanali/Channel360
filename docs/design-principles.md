# Channel360 — Enterprise Architecture & Design Principles

## Core Philosophy

Channel360 is not a reporting application.

Channel360 is a platform consisting of three major domains:

1. Platform Management
2. Security & Access Management
3. Channel Operations

The architecture must be designed for long-term scalability, configurability, and enterprise maintainability.

Avoid shortcuts that will create technical debt later.

---

## Role Design Principles

Channel360 separates Platform Administration from Business Administration.

### SUPER_ADMIN

**Purpose:** Platform Owner

**Responsibilities:**
- User Management
- Role Management
- Permission Management
- Menu Management
- CMS Management
- Popup Management
- Region Management
- Approval Workflow Configuration
- System Configuration

**Can:**
- Create Users
- Create Roles
- Assign Permissions
- Configure Menus
- Configure Approval Chains
- Configure Regions
- Manage CMS

**Cannot:**
- View Channel Data
- View Claims
- View Reports
- Approve Transactions
- Approve Claims
- Approve Business Requests

SUPER_ADMIN manages the platform but not the business.

---

### ADMIN

**Purpose:** Business Administrator

**Responsibilities:**
- Channel Operations
- Business Approvals
- Reports
- Claims
- Escalations

**Can:**
- View All Data
- View All Regions
- Approve Any Request
- Override Approval Chains
- Grant Access Directly
- Manage Business Operations

ADMIN is the highest business authority.

---

### MANAGER

**Purpose:** Region Scoped Approver

**Responsibilities:**
- Review Requests
- Approve Transactions
- Manage Assigned Regions

**Can:**
- View Assigned Region Data
- Approve Requests Within Scope

**Important:** Do not create separate roles per hierarchy level (TERRITORY_MANAGER, STATE_MANAGER, REGIONAL_MANAGER, ZONE_MANAGER). Role is always `MANAGER`. Authority is derived from assigned region.

```
Role = MANAGER
Assigned Region = Karnataka       => State Manager

Role = MANAGER
Assigned Region = South India     => Regional Manager
```

---

### INTERNAL_EMPLOYEE

Company employee. Examples: Operations, Sales, Finance, Product Team.

---

### EXTERNAL_EMPLOYEE

Contractors and outsourced users. Limited access.

---

### DISTRIBUTOR

Distributor users. Automatically scoped to their company. Cannot view other companies.

---

### CHANNEL_PARTNER

Partner users. Automatically scoped to their company. Cannot view other companies.

---

### GUEST

Temporary access granted by ADMIN.

---

## Important Design Rule

Do NOT implement roles using Java Enums.

**Bad:**
```java
enum Role { SUPER_ADMIN, ADMIN }
```

**Good:**
```sql
roles table
```

Reason: Roles should be configurable without code deployment.

---

## Permission Architecture

Do NOT implement permissions using enums.

**Bad:**
```java
enum Permission { USERS_CREATE }
```

**Good:**
```sql
permissions table
```

Example permissions: `users.create`, `users.edit`, `users.delete`, `sales.view`, `sales.create`, `sales.approve`, `claims.view`, `claims.approve`, `reports.view`.

New permissions must be configurable via database. No code deployment required.

---

## Database Driven Access Control

### Required Tables

- `roles`
- `permissions`
- `role_permissions`
- `user_roles`
- `user_access_grants`

### Access Evaluation

1. Role Permissions (inherited base permissions)
2. Region Assignment (geographic scope)
3. User Access Grants (individual overrides/grants)

Final access is determined by combining all three.

---

## Region Architecture

Use a self-referencing hierarchy.

### Table: `regions`

| Column | Type |
|--------|------|
| id | BIGSERIAL |
| name | VARCHAR |
| parent_id | BIGINT (self-ref) |
| level | VARCHAR |
| tree_type | VARCHAR |
| path | TEXT |

### Levels

- Zone
- Region
- State
- Territory

### Examples

```
Asia
Asia/South India
Asia/South India/Karnataka
Asia/South India/Karnataka/Mysore
```

Do not create separate tables for each level. Use one hierarchy table.

---

## Approval Architecture

Do NOT hardcode approval chains.

**Bad:**
```java
// Territory → State → Region (hardcoded)
```

**Good:**
```sql
approval_workflows table
approval_workflow_steps table
```

### Examples

- Access Request Workflow: State Manager → Regional Manager
- Batch Upload Workflow: Regional Manager → Admin
- Claims Workflow: State Manager → Admin

SUPER_ADMIN must be able to configure workflows dynamically. No code changes required.

---

## User Region Assignment

### Table: `user_regions`

| Column | Type |
|--------|------|
| user_id | BIGINT |
| region_id | BIGINT |
| assignment_type | VARCHAR |

### Assignment Types

- PRIMARY
- SECONDARY
- TEMPORARY

A user may belong to multiple regions.

---

## Access Request System

### Tables

- `access_requests`
- `user_access_grants`

### Requirements

- Prevent duplicate active requests
- Approval routing through workflow engine
- ADMIN can bypass approval chain
- Permanent grants until revoked
- Full audit trail

---

## Audit Requirements

Mandatory from Day One.

### Table: `audit_logs`

| Column | Type | Description |
|--------|------|-------------|
| user_id | BIGINT | Who performed the action |
| module_name | VARCHAR | Which module (users, sales, claims) |
| action_name | VARCHAR | CREATE, UPDATE, DELETE, APPROVE, REJECT |
| entity_name | VARCHAR | Which entity type |
| entity_id | BIGINT | Which record |
| old_value | JSONB | Previous state |
| new_value | JSONB | New state |
| created_at | TIMESTAMP | When |

### Examples

- Who approved an upload?
- Who changed a city mapping?
- Who granted access?
- Who modified a product master record?

Every important action must be auditable.

---

## File Management

Do not store file URLs across multiple tables.

### Table: `files`

| Column | Type |
|--------|------|
| id | BIGSERIAL |
| file_name | VARCHAR |
| storage_path | TEXT |
| uploaded_by | BIGINT |
| uploaded_at | TIMESTAMP |

All modules should reference file IDs. Use Supabase Storage.

---

## Master Data Governance

| Master | Editable By |
|--------|-------------|
| City Master | ADMIN |
| Product Master | ADMIN |
| Partner Master | ADMIN |
| Customer Master | ADMIN, Operations Team |

Avoid allowing external users to modify master data.

---

## Reporting Architecture

When reporting modules are introduced, do NOT query transactional tables directly.

Create:
- Reporting Views
- Materialized Views
- Fact Tables

### Examples

- `fact_sales`
- `fact_transfers`
- `fact_activations`

Design for millions of records.

---

## Recommended Development Roadmap

| Phase | Features |
|-------|----------|
| 1 | Authentication, Forgot Password, Reset Password, JWT, RBAC |
| 2 | CMS, Homepage, Popup Management, Light/Dark Theme |
| 3 | Users, Roles, Permissions, Regions |
| 4 | Approval Workflow Engine, Access Requests, Access Grants |
| 5 | City Master, Product Master, Partner Master, Customer Master |
| 6 | Daily Sales, Channel Entries, Transfers, Activations |
| 7 | Claims, Lifecycle Visibility |
| 8 | Reports, Analytics, External Data Uploads |

---

## Critical Rules — Never Violate

1. **Never hardcode roles in enums** — Roles must be DB-driven, configurable without deployment.
2. **Never hardcode permissions in enums** — Permissions must be DB-driven, `module.action` format.
3. **Never hardcode approval chains** — Workflows must be configurable via `approval_workflows` table.
4. **Never create separate manager roles per hierarchy level** — One `MANAGER` role, authority from region assignment.
5. **Keep SUPER_ADMIN separate from business operations** — Platform only, no channel data access.
6. **Keep ADMIN as highest business authority** — Full operational access, no platform config.
7. **Make workflows configurable through database** — SUPER_ADMIN configures, engine executes.
8. **Make permissions configurable through database** — SUPER_ADMIN assigns, code never hardcodes.
9. **Keep full audit history** — Every action logged via `audit_logs`.
10. **Design every module with region-based scoping in mind** — Data visibility follows region tree.
11. **Assume the system will eventually contain millions of records** — Design for scale from day one.
