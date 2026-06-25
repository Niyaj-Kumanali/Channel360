# Channel360 — Enterprise Architecture

This document defines the system architecture, design principles, modular monolith rules, business domain model, and all architecture decisions for Channel360.

> **See also:**
> - [README](README.md) — Project overview, tech stack, build plan
> - [Engineering Standards](engineering-standards.md) — Coding standards, DB-first, frontend/backend patterns
> - [API Standards](api-standards.md) — Response envelopes, pagination, error format

---

## 1. Purpose

Channel360 is not a reporting application. It is a platform consisting of three major domains:

1. **Platform Management** — Users, Roles, Permissions, Menus, CMS, Popups, Regions, Workflows
2. **Security & Access Management** — Access Requests, Access Grants, Approval Engine
3. **Channel Operations** — Master Data, Transactions, Claims, Reports, Analytics

The architecture is designed for long-term scalability, configurability, and enterprise maintainability. Shortcuts that create technical debt are prohibited.

---

## 2. System Topology

```
+-------------------------------------------------------+
|                 Frontend Client Layer                 |
|           React 19 SPA (Vite, Tailwind CSS)           |
+---------------------------+---------------------------+
                            |
                   HTTPS (JSON / JWT)
                            |
                            v
+-------------------------------------------------------+
|              Spring Boot 3 REST API Layer             |
|  +-------------------------------------------------+  |
|  | Security Filter Chain (Stateless JWT Validator) |  |
|  +------------------------+------------------------+  |
|                           |                           |
|                           v                           |
|  +-------------------------------------------------+  |
|  | Controller Layer (ApiResponse / PageResponse)   |  |
|  +------------------------+------------------------+  |
|                           |                           |
|                           v                           |
|  +-------------------------------------------------+  |
|  | Service Domain Layer (Business Rules & Engine)  |  |
|  +------------------------+------------------------+  |
|                           |                           |
|                           v                           |
|  +-------------------------------------------------+  |
|  | Repository Data Abstraction Layer (Spring Data) |  |
|  +-------------------------------------------------+  |
+---------------------------+---------------------------+
                            |
                  JPA / Stored Procedures
                            |
                            v
+-------------------------------------------------------+
|                PostgreSQL Storage Layer               |
|  +-------------------------------------------------+  |
|  | RBAC Tables (Roles, Permissions, Grants)        |  |
|  +-------------------------------------------------+  |
|  | Hierarchy Trees (Self-Referencing Regions)       |  |
|  +-------------------------------------------------+  |
|  | Transaction Processing & Stored Procedures      |  |
|  +-------------------------------------------------+  |
+-------------------------------------------------------+
```

### Shared Boundary Definitions
- **State Management Isolation:** React components remain layout wrappers. Global client state caching is isolated to TanStack Query caches.
- **Hybrid Database Paradigm:** Standard relations through JPA entities for lightweight reads. Deep validation, multi-row calculations, and pipeline entries use stored procedures (`sp_*`).
- **Audit Trail Integration:** Every modification relies on an interceptor hooking changes into `audit_logs` with JSONB diffs.

---

## 3. Design Principles

### SOLID Applied

| Principle | Frontend | Backend |
|-----------|----------|---------|
| **S**ingle Responsibility | Each hook/api/component has one job | Each service/repository owns one domain |
| **O**pen/Closed | Components extend via props/composition | Services extend via interfaces |
| **L**iskov Substitution | Polymorphic components (Button variants) | Repository abstraction (JPA ↔ Procedure) |
| **I**nterface Segregation | Focused hook return types | Focused DTOs per operation |
| **D**ependency Inversion | Hooks depend on api-client (not fetch directly) | Services depend on repository interfaces |

### KISS
- One pattern for API calls: `api-client.ts` → feature `api/*.ts` → React Query hook → Component
- One pattern for forms: Zod schema → React Hook Form → mutation hook
- One pattern for error handling: api-client throws → mutation catches → toast shows
- Flat over nested: `components/ui/` not `shared/components/ui/elements/`

### DRY
- Shared UI primitives in `components/ui/` (Button, Input, Card, etc.)
- Shared utils in `lib/` (api-client, cn, date formatting)
- Domain logic lives in features, never duplicated

### YAGNI
Build only what's required. Don't over-engineer for hypothetical futures.

---

## 4. Modular Monolith Architecture Rules

### Core Principle

```
Bad:  Monolith Today → Rewrite Later
Good: Modular Monolith Today → Extract Modules Tomorrow
```

Future migration should be evolutionary, not a rewrite.

### Current Module Structure

```
com.channel360
├── auth
├── users
├── roles
├── permissions
├── menus
├── cms
├── popup
├── regions
├── workflows
├── access
├── common
└── Channel360Application
```

Each module owns `api/`, `application/`, `domain/`, `infrastructure/`.

### Layer Responsibilities

| Layer | Purpose | Contains |
|-------|---------|----------|
| `api` | Expose functionality to other modules | Facades, Public DTOs, Public Contracts (visible to other modules) |
| `application` | Business use cases | Services, Use Cases, Commands, Queries |
| `domain` | Business model | Entities, Domain Rules, Value Objects |
| `infrastructure` | Technical implementation | JPA Repositories, Stored Procedure Calls, External Integrations |

### Critical Rule #1: Never Import Another Module's Repository

```java
// FORBIDDEN — inside User module:
@Autowired private RoleRepository roleRepository;

// CORRECT — use facades:
@RequiredArgsConstructor
public class UserService {
    private final RoleFacade roleFacade;
}
```

Only the `api` package of a module is visible to other modules. Repositories are internal implementation details.

### Critical Rule #2: Never Share Entities Across Modules

```java
// FORBIDDEN — outside Role module:
Role role = roleRepository.findById(id);

// CORRECT — use DTOs:
RoleResponseDto role = roleFacade.findById(id);
```

Only DTOs cross module boundaries.

### Critical Rule #3: No Cross-Module JPA Relationships

```java
// FORBIDDEN:
@ManyToOne private Role role;       // inside User entity
@OneToMany private List<User> users; // inside Role entity

// CORRECT — store IDs, resolve through Facades:
private Long roleId;
// ...
RoleDto role = roleFacade.findById(user.getRoleId());
```

### Critical Rule #4: No Circular Dependencies

```
FORBIDDEN:          CORRECT:
User                 Workflow
 ↓                    ↓
Role                  Region API
 ↓                    ↑
User                 (Region never depends on Workflow)
```

Circular dependencies block extraction.

**Resolved in codebase:**
- **Auth ↔ User cycle:** Removed `UserFacade` injection from `AuthFacadeImpl`; `AuthFacadeImpl` now calls `AuthUserRepository` directly for auth-related operations. `AuthService` no longer injects `AuthFacade`.
- **SecurityConfig ↔ JwtAuthenticationFilter ↔ CustomUserDetailsService cycle:** Extracted `PasswordEncoder` bean to dedicated `PasswordEncoderConfig` class, breaking the circular bean dependency.

### Critical Rule #5: Use Events for Side Effects

```java
// AVOID — mixing concerns in one service:
createUser();
sendEmail();
createAudit();
createNotification();

// PREFER — domain events:
UserCreatedEvent → AuditListener
                  → NotificationListener
                  → EmailListener
```

Current implementation may remain synchronous. Future microservices can consume the same events.

**Implemented (7 events at 6 action points):**
- `UserCreatedEvent` — published by `UserService.createUser()`
- `RoleAssignedEvent` — published by `UserService.assignRoles()`
- `RoleCreatedEvent` — published by `RoleService.createRole()`
- `RoleUpdatedEvent` — published by `RoleService.updateRole()`
- `WorkflowCreatedEvent` — published by `WorkflowService.createWorkflow()`
- `WorkflowApprovedEvent` — published by `ApprovalService.checkAndUpdateRequestStatus()` on full approval

Events carry only IDs and primitive fields (never entities or full DTOs). All publishers inject `ApplicationEventPublisher` via constructor.

---

## 4.x Enforced Module Boundaries

The following cross-module violations have been identified and fixed:

| Violation | Fix |
|-----------|-----|
| `MenuFacade` returned JPA entities (`MenuItem`, `Permission`) instead of DTOs | Replaced 5 entity-returning methods with DTO-returning equivalents (`MenuResponse`, `List<MenuResponse>`) |
| `AuthController` injected `MenuApplicationService` directly (from another module) | Changed to inject `MenuFacade` and call `getCurrentUserMenu()` |
| `UserMapper` referenced `RoleMapper` from role module via `uses = {RoleMapper.class}` | Removed cross-module reference; roles populated in `UserService` via `RoleFacade` |
| `AuthMapper.java` imported `user.domain.User` from another module | Deleted (dead code, was unused) |
| `MenuService` lived in `common/service/` instead of owning module | Moved to `menu/application/MenuApplicationService` |

### Enforcement Rules

1. **Only `api/` package is public** — `application/`, `domain/`, `infrastructure/` are module-private
2. **Facades are the only cross-module entry point** — no direct repository, service, or mapper access
3. **DTOs (records) are the only cross-module data type** — no entities cross boundaries
4. **Grep `import com.channel360.<other_module>` regularly** — any import of another module's `domain`, `application`, or `infrastructure` is a violation

---

## 5. Future Microservice Targets

| Service | Modules |
|---------|---------|
| **Identity Service** | auth, users, roles, permissions, menus |
| **Organization Service** | regions, region approvers |
| **Workflow Service** | workflows, access requests, access grants |
| **Master Data Service** | city master, product master, partner master, customer master |
| **Channel Operations Service** | daily sales, channel entries, transfers, activations, claims |
| **Reporting Service** | reports, analytics, dashboards |

---

## 6. Role Design

Channel360 strictly decouples **Platform Administration** from **Business Administration**. Roles are DB-driven, not enum-driven. Authority comes from region assignment, not role name.

### SUPER_ADMIN (Platform Owner)

**Responsibilities:** User Management, Role Management, Permission Management, Menu Management, CMS Management, Popup Management, Region Management, Approval Workflow Configuration, System Configuration.

**Can:** Create Users, Create Roles, Assign Permissions, Configure Menus, Configure Approval Chains, Configure Regions, Manage CMS.

**Cannot:** View Channel Data, View Claims, View Reports, Approve Transactions, Approve Claims, Approve Business Requests.

SUPER_ADMIN manages the platform but not the business.

### ADMIN (Business Administrator)

**Responsibilities:** Channel Operations, Business Approvals, Reports, Claims, Escalations.

**Can:** View All Data, View All Regions, Approve Any Request, Override Approval Chains, Grant Access Directly, Manage Business Operations.

ADMIN is the highest business authority.

### MANAGER (Region Scoped Approver)

**Responsibilities:** Review Requests, Approve Transactions, Manage Assigned Regions.

**Can:** View Assigned Region Data, Approve Requests Within Scope.

**Design Constraint:** Do NOT create separate roles per hierarchy level. There is only one `MANAGER` role. Authority is derived from assigned region:

```
Role = MANAGER, Region(State) = Karnataka       => State Manager
Role = MANAGER, Region = South India            => Regional Manager
Role = MANAGER, Region(Country) = India         => National Manager
```

### INTERNAL_EMPLOYEE
Company employee (Operations, Sales, Finance, Product Team). Internal access level with company-wide operational tools.

### EXTERNAL_EMPLOYEE
Contractors and outsourced users. Limited operational access. Cannot view internal company data outside their scope.

### DISTRIBUTOR
Distributor partner users. Automatically scoped to their company record. Cannot view other companies' data. Can upload daily sales, manage inventory within their scope.

### CHANNEL_PARTNER
Channel partner users. Automatically scoped to their company record. Cannot view other companies' data. Can manage customer sales and product activations.

### GUEST
Temporary access granted by ADMIN. Minimal dashboard view. Default role assigned at registration.

---

## 7. Permission Architecture

### No Java Enums for Roles or Permissions

Roles and granular permissions (`users.create`, `claims.approve`) are managed using database relational lookup tables. This allows modifications without software redeployments.

Technical transaction indicators (e.g., `Status.ACTIVE`, `RequestStatus.PENDING`) must use standard Java Enums.

### Permission Format

All permissions use `module.action` format:
- `users.view`, `users.create`, `users.edit`, `users.delete`
- `sales.view`, `sales.approve`
- `claims.view`, `claims.process`

### Database-Driven Access Control

Required tables: `roles`, `permissions`, `role_permissions`, `user_roles`, `user_regions`, `access_requests`, `user_access_grants`.

Access evaluation order:
1. **Role Permissions** — Base permissions inherited from assigned roles
2. **Region Assignment** — Geographic scope limiting data visibility
3. **User Access Grants** — Individual permission overrides/grants

```
Final access = Role Permissions ∩ Region Scope ∪ Access Grants
```

---

## 8. Permissions Catalog

### Platform Management

| Permission | Module | Description |
|------------|--------|-------------|
| `users.view` | users | View user list and details |
| `users.create` | users | Create new users |
| `users.edit` | users | Edit existing users |
| `users.delete` | users | Delete/deactivate users |
| `roles.view` | roles | View role list and details |
| `roles.create` | roles | Create new roles |
| `roles.edit` | roles | Edit existing roles |
| `roles.delete` | roles | Delete roles |
| `permissions.assign` | permissions | Assign permissions to roles |
| `menus.configure` | menus | Configure sidebar menu items |

### CMS Administration

| Permission | Module | Description |
|------------|--------|-------------|
| `sections.view` | sections | View homepage sections |
| `sections.create` | sections | Create homepage sections |
| `sections.edit` | sections | Edit homepage sections |
| `sections.delete` | sections | Delete homepage sections |
| `popups.view` | popups | View popups |
| `popups.create` | popups | Create popups |
| `popups.edit` | popups | Edit popups |
| `popups.delete` | popups | Delete popups |

### Geographic Scopes

| Permission | Module | Description |
|------------|--------|-------------|
| `regions.view` | regions | View region hierarchy |
| `regions.create` | regions | Create regions |
| `regions.edit` | regions | Edit regions |
| `regions.delete` | regions | Delete regions |

### Approval Workflows

| Permission | Module | Description |
|------------|--------|-------------|
| `workflows.view` | workflows | View approval workflows |
| `workflows.configure` | workflows | Configure approval workflow steps |

### Access Control

| Permission | Module | Description |
|------------|--------|-------------|
| `access.view` | access | View access requests and grants |
| `access.approve` | access | Approve/reject access requests |
| `access.grant` | access | Grant direct access (ADMIN bypass) |

### Operational Areas

| Permission | Module | Description |
|------------|--------|-------------|
| `sales.view` | sales | View daily sales data |
| `sales.upload` | sales | Upload daily sales invoices |
| `sales.approve` | sales | Approve batch corrections |
| `entries.view` | entries | View channel entries |
| `entries.create` | entries | Create channel entries |
| `transfers.view` | transfers | View partner transfers |
| `transfers.create` | transfers | Create partner transfers |
| `activations.view` | activations | View product activations |
| `activations.create` | activations | Create product activations |

### Claims

| Permission | Module | Description |
|------------|--------|-------------|
| `claims.view` | claims | View claims |
| `claims.create` | claims | Submit claims |
| `claims.process` | claims | Process/approve claims |

### Master Data

| Permission | Module | Description |
|------------|--------|-------------|
| `cities.view` | cities | View city master |
| `cities.create` | cities | Create city records |
| `cities.edit` | cities | Edit city records |
| `cities.import` | cities | CSV import city data |
| `products.view` | products | View product master |
| `products.create` | products | Create product records |
| `products.edit` | products | Edit product records |
| `products.import` | products | CSV import product data |
| `partners.view` | partners | View partner records |
| `partners.create` | partners | Create partner records |
| `partners.edit` | partners | Edit partner records |
| `customers.view` | customers | View customer records |
| `customers.create` | customers | Create customer records |
| `customers.edit` | customers | Edit customer records |

### Reporting

| Permission | Module | Description |
|------------|--------|-------------|
| `reports.view` | reports | View standard reports |
| `reports.create` | reports | Create custom reports |
| `analytics.view` | analytics | View analytics dashboards |
| `data.upload` | data | Upload external data |

---

## 9. Region Architecture

Geographies use a self-referencing hierarchy inside a single `regions` table.

### Levels

Zone → Region → State → Territory

### Table: `regions`

| Column | Type |
|--------|------|
| id | BIGSERIAL |
| name | VARCHAR |
| parent_id | BIGINT (self-referencing FK) |
| level | VARCHAR (Zone, Region, State, Territory) |
| tree_type | VARCHAR (B2B, B2C) |
| path | TEXT (materialized path for query performance) |

### Examples

```
Asia
Asia/South India
Asia/South India/Karnataka
Asia/South India/Karnataka/Mysore
```

Do NOT create separate tables for each level (no `zones`, `states`, `territories` tables).

---

## 10. Approval Architecture

Approval chains are NOT hardcoded. They are stored in configurable database tables.

### Tables

- `approval_workflows` — Workflow definitions (name, module, active, soft-deletable)
- `approval_workflow_steps` — Step definitions (step_order, approver_role, sla_hours, escalation_role, mandatory)
- `approval_requests` — Request instances (status: PENDING/APPROVED/REJECTED)
- `approval_tasks` — Individual approver tasks within a request

### Example Workflows

| Workflow | Steps |
|----------|-------|
| Access Request | State Manager → Regional Manager → ADMIN |
| Batch Upload Exception | Regional Manager → ADMIN |
| Claims | State Manager → ADMIN |

SUPER_ADMIN configures workflows dynamically. No code changes required.

### Approval Flow

1. **Workflow Definition:** ADMIN configures workflows + steps via API. Each step specifies a `roleName`, step order, optional SLA/escalation.
2. **Region Approver Assignment:** ADMIN assigns users as approvers for specific (region + role) combos via `region_approvers` table.
3. **Request Creation:** System creates `ApprovalRequest` (PENDING) + `ApprovalTask` per step. For each step, `resolveApprover()` walks the region hierarchy (specific → parent → grandparent) to find the assigned user.
4. **Approval/Rejection:** Approvers act on tasks. If all tasks approved → request becomes `APPROVED` + publishes `WorkflowApprovedEvent`. If any rejected → entire request becomes `REJECTED`.

---

## 11. Access Request System

- Users submit requests for specific permission + region scope
- Duplicate active requests are blocked until resolution
- Approval routing goes through workflow engine (chain of approvers)
- ADMIN can bypass the approval chain and grant directly
- Grants are permanent until explicitly revoked
- Full audit trail via `audit_logs`

### Tables

- `access_requests` — Pending access requests
- `user_access_grants` — Granted individual permissions (permanent until revoked)

---

## 12. Audit Requirements

Mandatory from Day One. Every important action must be auditable:
- Who approved an upload?
- Who changed a city mapping?
- Who granted access?
- Who modified a product master record?

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

---

## 13. Master Data Governance

| Master Data | Editable By |
|-------------|-------------|
| City Master | ADMIN |
| Product Master | ADMIN |
| Partner Master | ADMIN |
| Customer Master | ADMIN, Operations Team |

External users (DISTRIBUTOR, CHANNEL_PARTNER) cannot modify master data.

---

## 14. Reporting Architecture

Do NOT query transactional tables directly for reporting. Create:
- Reporting Views
- Materialized Views (for performance)
- Fact Tables (`fact_sales`, `fact_transfers`, `fact_activations`)

Design for millions of records.

---

## 15. File Management

Do not store file URLs across multiple tables. Use a single `files` table:

| Column | Type |
|--------|------|
| id | BIGSERIAL |
| file_name | VARCHAR |
| storage_path | TEXT |
| uploaded_by | BIGINT (FK to users) |
| uploaded_at | TIMESTAMP |

All modules reference file IDs. Use Supabase Storage.

---

## 16. Business Operation Pipeline

```
Channel Entry ----> Partner Transfer ----> Customer Purchase ----> Product Activation
(Mfg → Dist)        (Dist → Partner)       (Partner → Client)      (Warranty/Telemetry)
```

**Data Enrichment Engine:** Transactions (like Daily Sales CSV ingestion) auto-resolve text inputs against City Master data to assign geographic IDs and link rows to parent territories.

---

## 17. Architecture Decision Records

### ADR 001: Separation of Platform Administration from Operational Actions
- **Status:** Approved
- **Context:** Early-stage designs often combine administrative actions and transactional reporting paths under a singular identity, exposing operational workflows to configuration errors.
- **Decision:** Strictly decouple SUPER_ADMIN platform privileges from operational ADMIN views. SUPER_ADMIN acts purely as an infrastructure manager; business-level permissions require explicit routing via operational rules.
- **Consequences:** Simplifies auditing and ensures platform configurations do not conflict with day-to-day business data access.

### ADR 002: Dynamic Database-Driven RBAC System (No Enums)
- **Status:** Approved (Amended)
- **Context:** Hardcoded role/permission configurations require redeployment when business rules change.
- **Decision (Revised):** Roles, permissions, and menu items must live entirely in database tables. Java enums for roles or permissions are strictly prohibited — even as convenience constants. Enums are restricted to static technical statuses only (e.g., `Status.ACTIVE`, `RequestStatus.PENDING`).
- **Consequences:** Configurations can be updated without deployment. The `RoleName` enum must be removed and replaced with string-based lookups.

### ADR 003: Self-Referencing Region Hierarchy (Single Table)
- **Status:** Approved
- **Context:** Multi-level geographic hierarchies required for both B2B and B2C trees. Separate tables per level create rigid schemas and complex cross-table queries.
- **Decision:** Single self-referencing `regions` table with `parent_id`, `level`, `tree_type`, and materialized `path`.
- **Consequences:** Flexible hierarchy management. Levels can be added/removed without schema changes. Queries use recursive CTEs or path-based lookups.

### ADR 004: Configurable Approval Workflow Engine
- **Status:** Approved
- **Context:** Approval chains differ per module and must be configurable by SUPER_ADMIN without code changes.
- **Decision:** Generic approval workflow engine backed by `approval_workflows` and `approval_workflow_steps` tables. Routes requests through the chain automatically.
- **Consequences:** Approval logic is data-driven. New workflows can be defined at runtime.

### ADR 005: MANAGER Role Uses Region Assignment for Authority
- **Status:** Approved
- **Context:** Traditional designs create separate roles per hierarchy level, leading to role explosion.
- **Decision:** Exactly one `MANAGER` role. Authority is determined by assigned region in `user_regions`. A manager assigned to a state can approve state-level; a manager assigned to a zone can approve zone-level.
- **Consequences:** No role explosion. Authority is clear from region assignment.

### ADR 006: Audit Trail from Day One
- **Status:** Approved
- **Context:** Enterprise compliance requires tracking who did what, when, and what changed. Retrofitting audit later is expensive.
- **Decision:** `audit_logs` table from day one with JSONB columns for structural diffs. Every important action creates an audit record.
- **Consequences:** Complete audit trail from first transaction. Slight write overhead, negligible with proper indexing.

### ADR 007: Database-First with Stored Procedures for Writes
- **Status:** Approved
- **Context:** JPA's automatic schema generation and entity management can lead to production surprises and performance issues. Direct ORM writes bypass validation logic.
- **Decision:** `ddl-auto: none` with explicit SQL schema management. Writes go through stored procedures (`sp_*`). Reads use JPA derived queries for simplicity.
- **Consequences:** More explicit schema management. Stored procedures ensure data integrity at the database level.

### ADR 008: Immutable DTOs via Java Records
- **Status:** Implemented
- **Context:** Mutable `@Data` DTOs can be modified after creation, leading to subtle bugs. Java 21 records provide immutability, compact constructors, and built-in `equals`/`hashCode`.
- **Decision:** All response DTOs use Java 21 `record` types with Lombok `@Builder`. Accessor pattern changes from `.getXxx()`/`.isXxx()` to `.xxx()`. Request DTOs that require Jackson `@RequestBody` deserialization may remain `@Data`.
- **Consequences:** 14 DTOs converted. Immutability guarantees data integrity across module boundaries. Call sites updated to record accessor pattern.

### ADR 009: Domain Events for Business Actions
- **Status:** Implemented
- **Context:** Services were mixing side effects (email, audit, notifications) with core business logic, violating Single Responsibility.
- **Decision:** Key business actions publish typed Spring `ApplicationEvent`. Events are synchronous by default; can be made async via `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`. Events carry only IDs and primitive fields, never entities or full DTOs.
- **Consequences:** 7 events published at 6 action points. Core services no longer depend on notification/audit services directly.

### ADR 010: JPA Auditing for Entity Timestamps
- **Status:** Implemented
- **Context:** `@CreatedDate` and `@LastModifiedDate` annotations were declared on `BaseEntity` but never populated because `@EnableJpaAuditing` was not configured. Hibernate inserted NULL for `created_at`/`updated_at`, violating `NOT NULL` constraints.
- **Decision:** Added `@EnableJpaAuditing` via `JpaConfig` class. Created `AuditorAware<String>` bean returning the current authenticated user's name or `"SYSTEM"` during unauthenticated operations (seeding, batch).
- **Consequences:** All entities now correctly populate `created_at`, `updated_at`, `created_by`, `last_modified_by` automatically via `AuditingEntityListener`.

### ADR 011: PasswordEncoder as Separate Configuration
- **Status:** Implemented
- **Context:** `SecurityConfig` imported `JwtAuthenticationFilter` which triggered `CustomUserDetailsService` → `AuthFacadeImpl` → `AuthService`, which required `PasswordEncoder` from `SecurityConfig`, creating a circular bean dependency.
- **Decision:** `PasswordEncoder` bean extracted from `SecurityConfig` to a dedicated `PasswordEncoderConfig` `@Configuration` class.
- **Consequences:** Circular dependency broken. `SecurityConfig` no longer depends on auth module beans at configuration time. All services that need `PasswordEncoder` can inject it from `PasswordEncoderConfig`.

---

## 18. Critical Rules — Never Violate

1. **Never hardcode roles in enums** — DB-driven only, configurable without deployment.
2. **Never hardcode permissions in enums** — DB-driven, `module.action` format.
3. **Never hardcode approval chains** — Configurable via `approval_workflows` + `approval_workflow_steps` tables.
4. **Never create separate manager roles per hierarchy level** — One `MANAGER` role, authority from region assignment.
5. **Keep SUPER_ADMIN separate from business operations** — Platform only, no channel data access.
6. **Keep ADMIN as highest business authority** — Full operational access, no platform config.
7. **Make workflows configurable through database** — SUPER_ADMIN configures, engine executes.
8. **Make permissions configurable through database** — SUPER_ADMIN assigns, code never hardcodes.
9. **Keep full audit history** — Every action logged via `audit_logs` with JSONB diffs.
10. **Design every module with region-based scoping in mind** — Data visibility follows region tree.
11. **Assume the system will eventually contain millions of records** — Design for scale from day one.
