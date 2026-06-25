# Channel360 — Engineering Standards

This document establishes engineering constraints, development methodologies, and coding standards. All developers and AI-assisted engineering tools must adhere to these instructions.

> See [Architecture](architecture.md) for design principles, modular monolith rules, and role/permission design.

---

## 1. Core Paradigm Constraints

### Phase 1 & Phase 2 Freeze
Do not refactor, rename, rewrite, or modify files under `features/auth/**`, security filters, theme providers, or the CMS component architecture. They are structurally frozen.

### Database-First Approach
- All database schema changes must be written as raw PostgreSQL scripts inside `db/schema.sql`, `db/seed.sql`, or `db/procedures/`.
- Spring Boot must run with `spring.jpa.hibernate.ddl-auto=none`.

### Stored Procedure Mandate
- Simple CRUD operations may use standard Spring Data JPA repository methods.
- Writes with complex validations or high throughput must call PostgreSQL procedures via `CREATE PROCEDURE` (not functions) using the `@Procedure` annotation or explicit `EntityManager` invocation.

---

## 2. Hybrid Database Strategy

| Use Case | Approach |
|----------|----------|
| Simple reads | JPA derived query (`findByEmail`, `existsByXxx`) |
| Writes | Stored procedure via `@Procedure` (void return) |
| Complex reads (search + paginate + REFCURSOR) | `EntityManager.createStoredProcedureQuery()` |
| DDL | `ddl-auto: none`; schema in `db/schema.sql` |
| Seed data | `db/seed.sql` |
| Procedures | `db/procedures/*.sql` (run by `DatabaseInitializer`) |

### Procedure Conventions
- `sp_<entity>_save` — INOUT `p_id`, INSERT or UPDATE via `IS NULL` check
- `sp_<entity>_delete` — IN `p_id`, soft delete
- `sp_<entity>_list` — REFCURSOR out, search/filter/paginate/sort params
- `sp_<entity>_count` — OUT integer, same filters as list
- All params named `p_<name>` (PostgreSQL convention)
- `@Param("p_<name>")` required on all `@Procedure` methods

---

## 3. Frontend Patterns (React 19 / TypeScript)

### Data Flow
```
Component → React Query Hook → feature/api/*.ts → lib/api-client.ts → Backend
                ↕
        TanStack Query Cache
```

### Rules
- NO direct `fetch`/`axios` in components — always through hooks
- NO API logic in providers — providers are passive state containers only
- NO `useEffect` for data fetching — use React Query queries
- Every mutation hook handles: loading state, success toast, error toast, cache invalidation
- Every form uses: Zod schema + React Hook Form + mutation hook
- NO prop drilling beyond 2 levels — use composition or context

### Form Handling
Every form must use `react-hook-form` bound to a `zod` schema:
```typescript
mode: "onBlur",
reValidateMode: "onBlur",
noValidate: true
```

### Styling & Theme
- No custom vanilla CSS rules unless introducing complex canvas calculations
- Use Tailwind utility classes
- Leverage Amber design tokens (`--primary`)

### Frontend Folder Structure
```
src/
├── app/                          # App shell
│   ├── App.tsx                   # Root component
│   ├── providers/                # Context providers
│   ├── router/                   # Route definitions
│   └── layouts/                  # Page layouts
├── features/                     # Feature modules
│   └── <feature>/
│       ├── api/                  # API function calls
│       ├── hooks/                # React Query hooks
│       ├── pages/                # Page-level components
│       ├── components/           # Feature-specific components
│       ├── schemas/              # Zod validation schemas
│       └── types/                # TypeScript interfaces
├── components/
│   └── ui/                       # Shared UI primitives (shadcn-style)
├── lib/
│   ├── api-client.ts             # HTTP client (fetch wrapper, token refresh, error throw)
│   └── utils.ts                  # cn(), date format, etc.
├── main.tsx
└── index.css
```

### Naming Conventions
- Files: `PascalCase` for components/pages, `camelCase` for hooks/api/utils
- Hooks: `use<Domain><Action>` — `useUsersQuery`, `useCreateUserMutation`, `useLogin`
- API functions: `getXxx`, `createXxx`, `updateXxx`, `deleteXxx`
- Schemas: `<domain>Schema` — `loginSchema`, `createUserSchema`
- Types: `<Domain>Response`, `<Domain>Request`, `<Domain>FormData`

---

## 4. Backend Patterns (Java 21 / Spring Boot 3)

### Package Layering
Each feature must encapsulate its own `dto/request/` and `dto/response/` sub-packages. Global shared DTO packages are prohibited to keep modules loosely coupled.

### Response Standardization
- Single Resource operations: Return `ApiResponse<T>`
- Paginated Collections: Return `PageResponse<T>` directly. Do not wrap a `PageResponse` within an `ApiResponse`.

### DTOs as Records
All response DTOs must be Java 21 `record` types with Lombok `@Builder`. Response DTOs use `.xxx()` accessor pattern (not `.getXxx()`/`.isXxx()`). Request DTOs that require Jackson `@RequestBody` deserialization may remain `@Data`.

### Constructor Injection Only
```java
// CORRECT
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}

// FORBIDDEN
@Autowired private UserRepository userRepository;
```

### FetchType.LAZY Default
All `@ManyToMany`, `@OneToMany`, `@ManyToOne` relationships must use `FetchType.LAZY`. Access lazy collections only within `@Transactional(readOnly = true)` methods. Use `JOIN FETCH` or `@EntityGraph` when eager loading is needed within a transaction.

### JPA Auditing
`@EnableJpaAuditing` is enabled via `JpaConfig`. `AuditorAware<String>` returns the current authenticated user's name or `"SYSTEM"` during unauthenticated operations (seeding, batch jobs). All entities extend `BaseEntity` which uses `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`.

### Schema-First (DDL)
All tables defined in `db/schema.sql` with explicit columns matching entity mappings. `spring.jpa.hibernate.ddl-auto: none`. Column names in schema must match Hibernate naming strategy (snake_case from camelCase field names).

### Soft Delete
Hard deletes are prohibited. All deletable tables use `deleted_flag = TRUE` pattern. Procedures must SET `deleted_flag`, not `DELETE FROM`.

### Audit Columns
Every table must have `created_by`, `created_at`, `updated_by`, `updated_at`, `deleted_flag`.

### Backend Folder Structure
```
com.channel360
├── common/
│   ├── config/           # App config, CORS, Jackson
│   ├── security/         # JWT filter, SecurityConfig, UserDetailsService
│   ├── exception/        # GlobalExceptionHandler, custom exceptions
│   └── dto/response/     # ApiResponse<T>, PageResponse<T>, ErrorResponse
├── <module>/
│   ├── api/              # REST controllers + facades + public DTOs
│   ├── application/      # Business logic (services)
│   ├── domain/           # JPA entities
│   └── infrastructure/   # JPA repositories + stored procedure calls
└── Channel360Application.java
```

---

## 5. Domain Events Pattern

### Publishing
Inject `ApplicationEventPublisher` and call `publishEvent(new XxxEvent(...))`:

```java
applicationEventPublisher.publishEvent(
    new UserCreatedEvent(this, savedUser.getId(), savedUser.getEmail())
);
```

### Event Conventions
- Extend `java.util.EventObject` for serializability
- Carry only IDs and primitive fields (never entities or full DTOs)
- Named `<BusinessAction>Event` (e.g., `UserCreatedEvent`, `RoleAssignedEvent`)
- Package in `domain/event/` within owning module

### Listening (future)
Use `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` for async processing after transaction commit. Current implementation is synchronous; listeners process in the same transaction.

---

## 6. Module Boundary Compliance

### Detection
- No other module's `Repository` should be `@Autowired` or injected
- No other module's `Entity` should be imported
- No other module's `Mapper` should be referenced

### Resolution
- Access other modules only through their public `api/Facade` interfaces
- DTOs (records) are the only objects that cross module boundaries
- If data from another module is needed, add a method to that module's Facade

### Enforcement
- Grep regularly: `import com.channel360.<other_module>.domain`, `.application`, `.infrastructure`
- Allowed pattern only: `import com.channel360.<other_module>.api.*`

---

## 7. Coding Standards

### General
- **Clean Code**: Meaningful names, methods under 20 lines, classes under 200 lines
- **Java**: Constructor injection only (no field injection), `var` for obvious types, Stream API with `.toList()`, `java.time` API, `Optional` only as return values
- **TypeScript**: Strict mode, no `any` (use `unknown` + type guard), explicit return types on hooks
- **CSS**: Tailwind utility classes only; no custom CSS files; CSS variables for design tokens
- **Imports**: `@/` alias for all internal imports; group: React → library → feature → relative

### Record Accessor Pattern
Response DTOs are records. Use `.xxx()` not `.getXxx()` or `.isXxx()`:
```java
// record accessor:
user.email()          // NOT user.getEmail()
user.deletedFlag()    // NOT user.isDeletedFlag()
user.roleNames()      // NOT user.getRoleNames()
```

### Silent Catch Elimination
Empty catch blocks are prohibited:
```java
// FORBIDDEN:
catch (Exception ignored) {}

// CORRECT — log and re-throw:
catch (ResourceNotFoundException e) {
    throw e;  // let expected exceptions propagate
}
catch (Exception e) {
    log.error("Unexpected error for {}: {}", resourceId, e.getMessage());
    throw new BusinessException("Operation failed");
}
```

### Error Handling
- Never swallow errors; always show feedback to user; log server-side
- Centralized exception handling via `GlobalExceptionHandler`
- Service layers must not catch generic `Exception` for business flows

### UI/UX Guidelines
- **Consistency**: Same Button/Input/Card components everywhere; no inline-styled elements
- **Feedback**: Toast on every success/error; loading spinner during mutations
- **Validation**: Inline field errors on blur; form-level errors on submit
- **Empty states**: Show meaningful message + illustration when list is empty
- **Error states**: Show error message + retry button when fetch fails
- **Loading states**: Skeleton or spinner while data loads; never blank page
- **Responsive**: Mobile-first; all pages work on 320px–1920px
- **Accessible**: Labels on all inputs; focus-visible rings; semantic HTML
- **Performance**: React Query stale/cache times; lazy load route pages

---

## 8. Database Conventions

- `snake_case` for columns and tables
- Every table: `created_by`, `created_at`, `updated_by`, `updated_at`, `deleted_flag`
- Soft delete (`deleted_flag = true`), never hard delete
- `uq_<table>_<column>` for unique constraints
- `idx_<table>_<column>` for indexes
- Composite PKs for join tables (`user_roles`)

---

## 9. Security Patterns

- JWT access token (short-lived) + refresh token (long-lived, 7 days)
- BCrypt for password hashing
- Stateless authentication (no HttpSession)
- `SecurityFilterChain` with per-request authorization
- CSRF disabled (stateless API)
- CORS restricted to frontend origin
- `GlobalExceptionHandler` catches all security exceptions → `ErrorResponse`

---

## 10. OpenCode Directives

When working on this repository, AI tools must:

1. Scan all active guidelines inside `/docs` before emitting code changes.
2. Ensure new database migrations are provided as distinct, runnable SQL commands.
3. Keep methods compact (under 20 lines) and highly focused.
4. Reference [Architecture](architecture.md) for role/permission/region architecture decisions.
5. Do NOT modify frozen Phase 1 or Phase 2 code unless explicitly instructed.

---

## 11. Documentation Maintenance

### Single Source of Truth
All structural rules, database design patterns, and engineering instructions live exclusively inside `/docs`. When building features or modifying configurations, developers must cross-reference these documents.

### Document Maintenance Lifecycle
```
[Identify System Change] → [Propose / Update Draft Docs] → [Review / Validate] → [Merge to Repo]
```

1. When a business shift or new feature requirement occurs, analyze architectural impact before changing code.
2. Update the relevant files to reflect changes. For core infrastructure changes, draft a new ADR in [Architecture](architecture.md#17-architecture-decision-records).
3. Review changes against core principles to prevent regressions.
4. Commit updated documentation alongside implementation code.
