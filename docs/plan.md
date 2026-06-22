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

## Phase 3 — Foundation 📋

| Step | Module | Description |
|------|--------|-------------|
| 1 | **Roles** | 8 roles: SUPER_ADMIN, ADMIN, MANAGER, INTERNAL_EMPLOYEE, EXTERNAL_EMPLOYEE, DISTRIBUTOR, CHANNEL_PARTNER, GUEST |
| 2 | **Permissions** | Granular permission system (`module.action`) for all modules. Seed data for SUPER_ADMIN gets all, others get base defaults |
| 3 | **Region Hierarchy** | `regions` table (B2B + B2C trees). Zone → Region → State → Territory. CRUD + admin page |
| 4 | **Approval Workflow Engine** | Reusable engine for multi-level approval routing through region hierarchy. Configurable per module |
| 5 | **User Management** | Full user CRUD with role assignment + region assignment (`user_regions`) + distributor/partner linkage |

## Phase 4 — Master Data 📋

| Step | Module | Description |
|------|--------|-------------|
| 6 | **City Master** | City → state → territory mapping. CSV import. Auto-resolution for transaction enrichment |
| 7 | **Product Master** | SKU/MTM catalog. CSV import. Product hierarchy |
| 8 | **Partner Master** | Distributor + Channel Partner company records |
| 9 | **Customer Master** | End-customer records linked via GSTIN |

## Phase 5 — Access Control 📋

| Step | Module | Description |
|------|--------|-------------|
| 10 | **Access Requests** | Request system: user requests permission + region scope. Cooldown logic prevents duplicates |
| 11 | **Access Grants** | Granted permissions (bypasses hierarchy if ADMIN). Grant checking middleware/service |
| 12 | **Approval Queue** | Single pending queue UI for MANAGERs/ADMINs. Approve/reject with hierarchy routing |

## Phase 6 — Transactions 📋

| Step | Module | Description |
|------|--------|-------------|
| 13 | **Daily Sales** | Invoice-level upload by DISTRIBUTOR. Auto-enrichment (city→region, MTM→product). Batch corrections need approval |
| 14 | **Channel Entries** | Manufacturer → Distributor movements |
| 15 | **Transfers** | Distributor → Partner movements |
| 16 | **Activations** | Product activation tracking |

## Phase 7 — Claims & Visibility 📋

| Step | Module | Description |
|------|--------|-------------|
| 17 | **Claims** | Channel claims and incentive program tracking |
| 18 | **Lifecycle Visibility** | End-to-end product journey dashboard (visual pipeline) |

## Phase 8 — Reporting 📋

| Step | Module | Description |
|------|--------|-------------|
| 19 | **Reports** | Per-role scoped report views |
| 20 | **Analytics** | Business intelligence dashboards |
| 21 | **External Data Upload** | Third-party / marketplace data ingestion |

---

## Design Principles

- **SOLID** — Single responsibility, Open/closed, Liskov substitution, Interface segregation, Dependency inversion
- **KISS** — Keep it simple, stupid. Prefer straightforward solutions over clever ones
- **DRY** — Don't repeat yourself. Extract shared logic into utilities and base classes
- **YAGNI** — You ain't gonna need it. Build only what's required, don't over-engineer for hypothetical futures
