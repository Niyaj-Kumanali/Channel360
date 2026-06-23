# Channel360 — Architecture Decision Records (ADRs)

This document catalogs core architecture decisions tracking critical infrastructure design justifications.

---

## ADR 001: Separation of Platform Administration from Operational Actions
- **Status:** Approved
- **Context:** Early-stage designs often combine administrative actions and transactional reporting paths under a singular administrative identity, exposing operational workflows to configuration errors.
- **Decision:** We strictly decouple `SUPER_ADMIN` platform privileges from operational `ADMIN` views. The `SUPER_ADMIN` acts purely as an infrastructure manager, while business-level permissions require explicit routing via operational rules.
- **Consequences:** This simplifies auditing and ensures that platform configurations do not conflict with day-to-day business data access.

---

## ADR 002: Dynamic Database-Driven RBAC System (No Enums)
- **Status:** Approved (Amended)
- **Context:** Hardcoded role and permission configurations require compiling and redeploying software artifacts when business rule requirements change. Initial implementation included a `RoleName` Java enum for convenience constants.
- **Decision (Revised):** Roles, permissions, and menu layout items must live entirely inside dynamic database tables. Java enums for roles or permissions are strictly prohibited — even as convenience constants. All role references in code must use string lookups against the database. Enums are restricted to static technical statuses only (e.g., `Status.ACTIVE`, `RequestStatus.PENDING`).
- **Consequences:** System configurations can be updated on the fly without deployment down-time. The `RoleName` enum must be removed and replaced with string-based lookups. New roles can be seeded without touching code.

---

## ADR 003: Self-Referencing Region Hierarchy (Single Table)
- **Status:** Approved
- **Context:** Multi-level geographic hierarchies (Zone → Region → State → Territory) are required for both B2B and B2C trees. Creating separate tables for each level would create rigid schemas and complex cross-table queries.
- **Decision:** Use a single self-referencing `regions` table with `parent_id` for hierarchy, `level` for depth, `tree_type` for B2B/B2C separation, and `path` for materialized path queries.
- **Consequences:** Flexible hierarchy management. SUPER_ADMIN can add/remove levels without schema changes. Queries use recursive CTEs or path-based lookups.

---

## ADR 004: Configurable Approval Workflow Engine
- **Status:** Approved
- **Context:** Approval chains differ per module (access requests, batch uploads, claims) and must be configurable by SUPER_ADMIN without code changes.
- **Decision:** Implement a generic approval workflow engine backed by `approval_workflows` and `approval_workflow_steps` tables. Each workflow defines a chain of approver roles at specific hierarchy levels. The engine routes requests through the chain automatically.
- **Consequences:** Approval logic is data-driven, not hardcoded. New workflows can be defined at runtime. Future business rule changes don't require deployments.

---

## ADR 005: MANAGER Role Uses Region Assignment for Authority
- **Status:** Approved
- **Context:** Traditional designs create separate roles per hierarchy level (STATE_MANAGER, REGIONAL_MANAGER, ZONE_MANAGER), leading to role explosion and complex permission management.
- **Decision:** There is exactly one `MANAGER` role. A manager's authority level is determined by their assigned region in the `user_regions` table. A manager assigned to a state can approve state-level; a manager assigned to a zone can approve zone-level.
- **Consequences:** No role explosion. One role serves all hierarchy levels. Authority is clear from region assignment. The approval workflow engine uses hierarchy level to determine which manager can approve.

---

## ADR 006: Audit Trail from Day One
- **Status:** Approved
- **Context:** Enterprise compliance requires tracking who did what, when, and what changed. Retrofitting audit later is expensive and often misses critical data.
- **Decision:** Implement `audit_logs` table from day one with JSONB columns (`old_value`, `new_value`) for structural diffs. Every important action (create, update, delete, approve, reject, grant) creates an audit record.
- **Consequences:** Complete audit trail from the first transaction. Slight write overhead, but negligible with proper indexing. Audit data supports compliance, debugging, and analytics.

---

## ADR 007: Database-First with Stored Procedures for Writes
- **Status:** Approved
- **Context:** JPA's automatic schema generation and entity management can lead to production surprises and performance issues at scale. Direct ORM writes bypass validation logic.
- **Decision:** Use `ddl-auto: none` with explicit SQL schema management. Writes go through stored procedures (`sp_*`), ensuring consistent validation and audit. Reads use JPA derived queries for simplicity.
- **Consequences:** More explicit schema management. Stored procedures ensure data integrity at the database level. Read queries benefit from JPA's convenience without write-path risks.
