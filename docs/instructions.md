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

## 2. Frontend Patterns (React 19 / TypeScript)
* **API Ingestion:** Direct execution of `fetch` or `axios` within UI components is strictly banned. Components must interact solely with custom React Query hooks (`useQuery` / `useMutation`) defined in the feature's `hooks/` folder.
* **Form Handling:** Every single form must utilize `react-hook-form` bound to a `zod` or `yup` schema. Validation behavior must match the enterprise design token:
```typescript
  mode: "onBlur",
  reValidateMode: "onBlur",
  noValidate: true
Styling & Theme tokens: No custom vanilla CSS rules are allowed unless introducing complex canvas calculations. Use Tailwind utility classes. Leverage the established Amber color design tokens (--primary).

3. Backend Patterns (Java 21 / Spring Boot 3)
Response Standardization: Every REST controller response must wrap returns using the global architectural envelope layout:

Single Resource operations: Return ApiResponse<T>

Paginated Collections: Return PageResponse<T> directly. Do not wrap a PageResponse within an ApiResponse.

Package Layering: Each feature must encapsulate its own dto/request/ and dto/response/ sub-packages. Global shared DTO packages are prohibited to keep modules loosely coupled.

4. OpenCode Directive Instructions
When working on this repository, you must:

Scan all active guidelines inside /docs before emitting code changes.

Ensure new database migrations are provided as distinct, runnable SQL commands.

Keep methods compact (under 20 lines) and highly focused.
