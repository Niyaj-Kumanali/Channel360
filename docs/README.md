# Channel360 — Channel Management & Intelligence Platform

**Channel360** is a centralized Channel Management and Intelligence Platform that provides organizations with complete visibility into their product journey, partner ecosystem, and business operations through a single unified portal.

The platform enables businesses to track and manage the complete lifecycle of products across the distribution network, from initial channel entry to end-customer engagement and product activation, with a secure and configurable environment for user management, content management, and partner communications.

---

## Architecture

```
Frontend (React SPA) ⇄ Backend (Spring Boot 3 REST API) ⇄ PostgreSQL
```

> See [Architecture](architecture.md) for detailed design principles, modular monolith rules, and business domain model.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 19, TypeScript, Vite 6, Tailwind CSS, TanStack Query, React Hook Form, Zod |
| State/Server | TanStack Query (React Query), React Hook Form, Zod |
| Routing | React Router v7 |
| Backend | Java 21, Spring Boot 3, Spring Security, Spring Data JPA |
| Auth | JWT (access + refresh tokens), BCrypt, RBAC |
| Database | PostgreSQL with stored procedures |
| Build | Maven (backend), Vite (frontend) |
| Email | Gmail SMTP (forgot/reset password flow) |

## Design System

| Token | Value |
|-------|-------|
| Brand color | Amber (`--primary: 38 92% 50%`) |
| App name | Lowercase `channel360` |
| Radius | `0.5rem` |
| Font | Inter |

## Development Setup

1. Clone the repository
2. **Backend:** `cd backend && mvn spring-boot:run` (uses `.env` for configuration)
3. **Frontend:** `cd frontend && npm run dev` (runs on `http://localhost:5174`)

### Environment

| Service | URL | Notes |
|---------|-----|-------|
| Backend | `http://localhost:5000` | Spring Boot 3, Java 21 |
| Frontend | `http://localhost:5174` | Vite dev server |
| Database | PostgreSQL on Supabase | Connection in `backend/.env` |
| Email | Gmail SMTP | App password in `backend/.env` |

---

## Build Plan & Progress

```
Phase 1 & 2 (FROZEN)
    ↓
Phase 3: Foundation (ACTIVE)
    ↓
Phase 4: Master Data (📋)
    ↓
Phase 5: Access Control (📋)
    ↓
Phase 6: Transactions (📋)
    ↓
Phase 7 & 8: Analysis (📋)
```

### Phase 1 — Authentication ✅ **FROZEN — No modifications allowed**

| Step | Status |
|------|--------|
| Login, Register, JWT auth with refresh tokens | ✅ |
| Forgot Password / Reset Password with email service | ✅ |
| RBAC (role-based access control foundation) | ✅ |
| User & Role management (CRUD, stored procedures) | ✅ |
| Security: BCrypt, SecurityFilterChain, CORS, PermissionAspect | ✅ |

### Phase 2 — Theme & CMS ✅ **FROZEN — No modifications allowed**

| Step | Status |
|------|--------|
| Dark/Light theme with persistent toggle (ThemeProvider, useTheme) | ✅ |
| CMS homepage sections: 11 types with inline SVGs, SectionRenderer | ✅ |
| Section manager (DnD reorder, create/edit slide-over with live preview) | ✅ |
| Popup management (priority, scheduling, CRUD) | ✅ |
| FAQ page at `/faq` with dual-mode (section shows 2, page shows all) | ✅ |
| All sections synced with backend (SECTION_TYPES, typeIcon, seed.sql) | ✅ |
| Tailwind warnings resolved, backend section_type validation added | ✅ |

### Phase 3 — Foundation 🔜 **Current**

| Step | Module | Status |
|------|--------|--------|
| 1 | **Roles** — Seed 8 identity roles in DB. Remove `RoleName` Java enum. Update `AuthService` default role to `ROLE_GUEST`. | ✅ Roles seeded. RoleName enum removal pending. |
| 2 | **Permissions** — Seed granular `module.action` permissions for all modules. Assign base permissions per role. | ✅ Permissions seeded and assigned to roles in seed.sql |
| 3 | **Approval Workflow Engine** — `approval_workflows` + `approval_workflow_steps` tables. Reusable multi-level approval routing with region hierarchy resolution. | ✅ Implemented and operational |
| 4 | **Regions** — Create `regions` table (self-referencing, B2B + B2C trees). Zone → Region → State → Territory levels. CRUD + admin page. | ⏳ Pending |
| 5 | **User Management** — Enhance user CRUD with role assignment + region assignment (`user_regions` table) + distributor/partner linkage. | ⏳ Pending |

### Phase 4 — Master Data 📋

| Step | Module |
|------|--------|
| 5 | City Master — City → state → territory mapping. CSV import. Auto-resolution for transaction enrichment. |
| 6 | Product Master — SKU/MTM catalog. CSV import. Product hierarchy. |
| 7 | Partner Master — Distributor + Channel Partner company records. |
| 8 | Customer Master — End-customer records linked via GSTIN. |

### Phase 5 — Access Control 📋

| Step | Module |
|------|--------|
| 9 | Access Requests — Request system with cooldown. Routes through workflow engine. |
| 10 | Access Grants — `user_access_grants` table. ADMIN bypass. Permanent grants. |
| 11 | Approval Queue — Single pending queue UI for MANAGER/ADMIN. |

### Phase 6 — Transactions 📋

| Step | Module |
|------|--------|
| 13 | Daily Sales — Invoice-level upload by DISTRIBUTOR. Auto-enrichment. Batch corrections need approval. |
| 14 | Channel Entries — Manufacturer → Distributor movements. |
| 15 | Transfers — Distributor → Partner movements. |
| 16 | Activations — Product activation tracking. |

### Phase 7 — Claims & Visibility 📋

| Step | Module |
|------|--------|
| 17 | Claims — Channel claims and incentive program tracking. |
| 18 | Lifecycle Visibility — End-to-end product journey dashboard. |

### Phase 8 — Reporting 📋

| Step | Module |
|------|--------|
| 19 | Reports — Per-role scoped report views. Materialized views/fact tables. |
| 20 | Analytics — Business intelligence dashboards. |
| 21 | External Data Upload — Third-party / marketplace data ingestion. |

---

## Completed Backend Refactoring

The backend has undergone systematic refactoring to enforce modular monolith boundaries, modernize Java 21 patterns, and eliminate technical debt:

| Phase | Work | Detail |
|-------|------|--------|
| **Phase 1** | Infrastructure | Constructor injection enforced, `@Builder.Default` added to entities, `BadRequestException` standardized |
| **Phase 2** | Circular Dependencies | Auth↔User cycle broken (removed `UserFacade` from `AuthFacadeImpl`). `SecurityConfig`↔`JwtAuthenticationFilter` cycle broken (extracted `PasswordEncoderConfig`) |
| **Phase 3** | Silent Catch Elimination | 13+ empty `catch (Exception ignored) {}` in `ApprovalService` replaced with `log.warn()` + proper propagation. Also fixed 3 in `RegionApproverService`, 1 in `AuditService`, 1 in `UserService` |
| **Phase 4** | Entity Consistency | `FetchType.EAGER` → `LAZY` on all relationships. `PermissionResponse` record created to stop leaking JPA entities from controllers |
| **Phase 5** | Module Boundaries | `MenuService` moved to proper module. `AuthController` fixed to use `MenuFacade` instead of direct `MenuApplicationService` injection |
| **Phase 6** | Dead Code Cleanup | Duplicate `AuthUserDto` deleted. 6 empty directories removed. `AuthMapper.java` deleted (dead cross-module code) |
| **Phase 7** | Cross-Module Violations | `MenuFacade` entity leaks fixed (DTOs replacing entities). `UserMapper` cross-module `RoleMapper` reference removed. `AuthService` narrowed generic `catch (Exception)` |
| **Phase 8** | Domain Events | 7 events published at 6 key business actions (`UserCreated`, `RoleAssigned`, `RoleCreated`, `RoleUpdated`, `WorkflowCreated`, `WorkflowApproved`) |
| **Phase 9** | DTO→Record Conversion | 14 response DTOs converted from mutable `@Data` to immutable Java 21 `record` types with `@Builder`. Updated 22 `.getXxx()` → `.xxx()` call sites across 6 files |

---

## Core Business Areas

- **Channel Entry** — Track product movement from manufacturers to distributors
- **Partner Transfer** — Track product movement between distributors and channel partners
- **Customer Purchase** — Track product sales from channel partners to end customers
- **Product Activation** — Activation visibility with complete lifecycle tracking
- **Claims Management** — Channel-related claims and incentive programs
- **External Data Integration** — Upload and manage data from external sources

## Platform Domains

Channel360 is a platform consisting of three major domains:

1. **Platform Management** — Users, Roles, Permissions, Menus, CMS, Popups, Regions, Workflows
2. **Security & Access Management** — Access Requests, Access Grants, Approval Engine
3. **Channel Operations** — Master Data, Transactions, Claims, Reports, Analytics
