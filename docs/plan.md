# Channel360 — Build Plan

## Phase 1 — Authentication ✅ **FROZEN**

- Login, Register, JWT auth with refresh tokens
- Forgot Password / Reset Password with email service
- RBAC (role-based access control foundation)

**→ FROZEN. No modifications allowed.**

## Phase 2 — Theme & CMS ✅ **FROZEN**

- Dark/Light theme management with persistent toggle
- CMS homepage sections (11 types, inline SVGs, SectionRenderer)
- Popup management with priority & scheduling
- FAQ page at `/faq`

**→ FROZEN. No modifications allowed.**

---

## Phase 3 — Foundation 📋 **CURRENT**

| Step | Module | Description |
|------|--------|-------------|
| 1 | **Roles** | Seed 8 identity roles in DB. Remove `RoleName` Java enum — use string constants. Update `AuthService` default role to `ROLE_GUEST`. Update `UserSeeder` and `AppConstants`. |
| 2 | **Permissions** | Seed granular `module.action` permissions for all modules. Assign base permissions per role. |
| 3 | **Regions** | Create `regions` table (self-referencing, B2B + B2C trees). Zone → Region → State → Territory levels. CRUD endpoints + admin page. |
| 4 | **User Management** | Enhance user CRUD with role assignment + region assignment (`user_regions` table) + distributor/partner linkage. |

## Phase 4 — Access Control 📋

| Step | Module | Description |
|------|--------|-------------|
| 5 | **Approval Workflow Engine** | `approval_workflows` + `approval_workflow_steps` tables. Reusable multi-level approval routing. SUPER_ADMIN configurable. |
| 6 | **Access Requests** | Request system: user requests permission + region scope. Cooldown prevents duplicates. Routes through workflow engine. |
| 7 | **Access Grants** | `user_access_grants` table. ADMIN bypasses hierarchy. Permanent grants. Grant checking middleware/service. |
| 8 | **Approval Queue** | Single pending queue UI for MANAGER/ADMIN. Approve/reject with hierarchy routing. |

## Phase 5 — Master Data 📋

| Step | Module | Description |
|------|--------|-------------|
| 9 | **City Master** | City → state → territory mapping. CSV import. Auto-resolution for transaction enrichment. |
| 10 | **Product Master** | SKU/MTM catalog. CSV import. Product hierarchy. |
| 11 | **Partner Master** | Distributor + Channel Partner company records. |
| 12 | **Customer Master** | End-customer records linked via GSTIN. |

## Phase 6 — Transactions 📋

| Step | Module | Description |
|------|--------|-------------|
| 13 | **Daily Sales** | Invoice-level upload by DISTRIBUTOR. Auto-enrichment (city→region, MTM→product). Batch corrections need approval. |
| 14 | **Channel Entries** | Manufacturer → Distributor movements. |
| 15 | **Transfers** | Distributor → Partner movements. |
| 16 | **Activations** | Product activation tracking. |

## Phase 7 — Claims & Visibility 📋

| Step | Module | Description |
|------|--------|-------------|
| 17 | **Claims** | Channel claims and incentive program tracking. |
| 18 | **Lifecycle Visibility** | End-to-end product journey dashboard (visual pipeline). |

## Phase 8 — Reporting 📋

| Step | Module | Description |
|------|--------|-------------|
| 19 | **Reports** | Per-role scoped report views. Materialized views/fact tables. |
| 20 | **Analytics** | Business intelligence dashboards. |
| 21 | **External Data Upload** | Third-party / marketplace data ingestion. |

---

## Design Principles

- **SOLID** — Single responsibility, Open/closed, Liskov substitution, Interface segregation, Dependency inversion
- **KISS** — Keep it simple, stupid. Prefer straightforward solutions over clever ones
- **DRY** — Don't repeat yourself. Extract shared logic into utilities and base classes
- **YAGNI** — You ain't gonna need it. Build only what's required, don't over-engineer for hypothetical futures
