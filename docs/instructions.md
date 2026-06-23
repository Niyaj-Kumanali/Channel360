# Channel360 — Engineering & OpenCode AI Coding Standards

This document establishes the strict engineering constraints, development methodologies, and coding standards for Channel360. All developers and AI-assisted engineering tools (e.g., OpenCode) must adhere to these instructions without deviation.

---

## 1. Core Paradigm Constraints

* **Phase 1 & Phase 2 Freeze:** Do not refactor, rename, rewrite, or modify files under `features/auth/**`, security filters, theme providers, or the CMS component architecture. They are structurally frozen.
* **Database-First Approach:** All database schema changes must be written as raw PostgreSQL script updates inside `db/schema.sql`, `db/seed.sql`, or `db/procedures/`. Spring Boot must run with `spring.jpa.hibernate.ddl-auto=none`.
* **Stored Procedure Mandate:**
  * Simple CRUD operations may use standard Spring Data JPA repository methods.
  * Writes with complex validations or high throughput must call PostgreSQL procedures via `CREATE PROCEDURE` (not functions) using the `@Procedure` annotation or explicit `EntityManager` invocation.

---

## 2. Role & Permission Rules

* **No Java Enums for Roles or Permissions:** Roles and permissions must live in database tables (`roles`, `permissions`, `role_permissions`). Java enums are strictly prohibited for role/permission constants — they must be removed if they exist. Use string constants or direct DB lookups instead.
* **DB-Driven Access Control:** All role-to-permission assignments are managed via `role_permissions` table. No code changes required for adding new roles or permissions.
* **Permission Format:** All permissions use `module.action` format (e.g., `users.create`, `sales.approve`).

---

## 3. Critical Rules — Never Violate

1. Never hardcode roles in enums.
2. Never hardcode permissions in enums.
3. Never hardcode approval chains — use `approval_workflows` + `approval_workflow_steps` tables.
4. Never create separate manager roles per hierarchy level — one MANAGER role, authority from region.
5. Keep SUPER_ADMIN separate from business operations — platform only.
6. Keep ADMIN as highest business authority — no platform config.
7. Make workflows configurable through database — SUPER_ADMIN configures.
8. Make permissions configurable through database — SUPER_ADMIN assigns.
9. Keep full audit history — `audit_logs` table with JSONB diffs.
10. Design every module with region-based scoping in mind — visibility follows region tree.
11. Assume the system will eventually contain millions of records — design for scale.

---

## 4. Frontend Patterns (React 19 / TypeScript)

* **API Ingestion:** Direct execution of `fetch` or `axios` within UI components is strictly banned. Components must interact solely with custom React Query hooks (`useQuery` / `useMutation`) defined in the feature's `hooks/` folder.
* **Form Handling:** Every single form must utilize `react-hook-form` bound to a `zod` or `yup` schema. Validation behavior:
```typescript
  mode: "onBlur",
  reValidateMode: "onBlur",
  noValidate: true
```
* **Styling & Theme tokens:** No custom vanilla CSS rules are allowed unless introducing complex canvas calculations. Use Tailwind utility classes. Leverage the established Amber color design tokens (--primary).

---

## 5. Backend Patterns (Java 21 / Spring Boot 3)

* **Response Standardization:** Every REST controller response must wrap returns using the global architectural envelope layout:
  * Single Resource operations: Return `ApiResponse<T>`
  * Paginated Collections: Return `PageResponse<T>` directly. Do not wrap a PageResponse within an ApiResponse.
* **Package Layering:** Each feature must encapsulate its own `dto/request/` and `dto/response/` sub-packages. Global shared DTO packages are prohibited to keep modules loosely coupled.
* **Soft Delete:** Hard deletes are prohibited. All deletable tables use `deleted_flag = TRUE` pattern. Procedures must SET deleted_flag, not DELETE FROM.
* **Audit Columns:** Every table must have `created_by`, `created_at`, `updated_by`, `updated_at`, `deleted_flag`.

---

## 6. OpenCode Directive Instructions

When working on this repository, you must:

1. Scan all active guidelines inside `/docs` before emitting code changes.
2. Ensure new database migrations are provided as distinct, runnable SQL commands.
3. Keep methods compact (under 20 lines) and highly focused.
4. Reference `docs/design-principles.md` for role/permission/region architecture decisions.
5. Do NOT modify frozen Phase 1 or Phase 2 code unless explicitly instructed by the user.
