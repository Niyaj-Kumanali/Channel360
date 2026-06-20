# Channel360 — Instructions

## ⚠️ PHASE 1 FREEZE — DO NOT MODIFY

The following Phase 1 (Auth) files must never be edited in future phases.
Build new features on top of them without altering them.

### Frontend (do not modify)
- `features/auth/` — entire directory (pages, hooks, api, schemas, types)
- `components/ui/FloatingLabelInput.tsx`
- `components/ui/PasswordInput.tsx`
- `components/ui/PasswordStrength.tsx`
- `lib/api-client.ts`
- `lib/api-error.ts`
- `lib/error-utils.ts`
- `lib/password-strength.ts`
- `main.tsx` (Toaster config)
- `index.css` (autofill override, custom animations)
- `app/layouts/AuthLayout.tsx`
- `app/providers/AuthProvider.tsx`
- `app/router/AppRouter.tsx` (auth route definitions)
- `app/App.tsx`

### Backend (do not modify)
- `auth/` — entire directory (controller, service, DTOs, entities, repos, mapper)
- `common/service/EmailService.java`
- `common/config/SecurityConfig.java`
- `common/config/WebConfig.java`
- `common/security/` — entire directory (JWT filter, provider, user details)
- `common/exception/` — entire directory
- `common/constants/AppConstants.java`

---

## Design Principles

- **SOLID** — Single responsibility, Open/closed, Liskov substitution, Interface segregation, Dependency inversion
- **KISS** — Keep it simple, stupid. Prefer straightforward solutions over clever ones
- **DRY** — Don't repeat yourself. Extract shared logic into utilities and base classes
- **YAGNI** — You ain't gonna need it. Build only what's required, don't over-engineer for hypothetical futures
- **Separation of Concerns** — Controllers handle HTTP, Services handle business logic, Repositories handle data access
- **Composition over Inheritance** — Prefer composing small, focused components/classes over deep inheritance hierarchies
- **Fail Fast** — Validate inputs early, throw meaningful exceptions immediately
- **Convention over Configuration** — Follow framework defaults unless there's a clear reason to override

---

## Project Constraints

- DB-first: `ddl-auto: none`, schema in `db/schema.sql`, seed in `db/seed.sql`
- All `CREATE PROCEDURE` (PostgreSQL), not functions
- Hybrid: JPA derived queries for simple reads; `@Procedure` or EntityManager for writes/complex reads
- Menu config from backend (not hardcoded in frontend)
- Admin creates users (no self-registration)
- `.env` at `backend/.env` (springboot3-dotenv)
- Three generic response DTOs: `ApiResponse<T>`, `PageResponse<T>`, `ErrorResponse`
- DTOs organized by `request/` and `response/` packages per module
- `PageResponse<T>` returned directly (not wrapped in `ApiResponse`)
- App name is lowercase "channel360"
- Amber primary color scheme (not blue)

---

## Key Decisions

### Validation & Forms
- **`mode: 'onBlur'` + `reValidateMode: 'onBlur'`** — validation only on blur, never on keystroke
- **noValidate on all forms** — suppress browser native validation completely
- **Required fields** — red asterisk `*` on label + red border on input; **no text errors** for required-field messages (filtered out by `!/required/i.test(error)` in FloatingLabelInput and PasswordInput)
- **yup v1** + `yupResolver` from `@hookform/resolvers/yup` v4
- **`min(6)` removed** from yup password schemas — password strength checklist handles it

### Error Handling
- **ApiError class** — carries `errors: string[]` + `statusCode`; thrown by `apiClient` on non-2xx
- **Error display**: backend `errors[]` → `setError` below inputs; top-level `message` → toast
- **Backend field errors** are regex-matched via `mapErrorToField` utility to determine which form field they belong to

### UI Patterns
- **Floating label via peer**: `placeholder-transparent` + `:not(:placeholder-shown)` + `peer-focus:` CSS-only technique. Label centered vertically at idle, floats to `-top-2.5` on focus/content
- **Password strength**: checklist with 5 criteria (6+ chars, uppercase, lowercase, number, symbol) shown as soon as user starts typing. 5-segment bar fills left-to-right
- **Toast**: `react-hot-toast`, `position="bottom-left"`, `containerStyle={{ bottom: 24 }}`. White card with rounded-xl, amber (success) or red (error) left 4px border accent
- **Autofill**: Chrome's blue background overridden via `-webkit-autofill` CSS with box-shadow inset and text-fill-color

### Backend Critical Knowledge
- **INOUT + REFCURSOR issue**: `spSave()` MUST return `void` + call `findByEmail()` after. REFCURSOR via `EntityManager.createStoredProcedureQuery()`, NOT `@Procedure`
- **PostgreSQL named binding**: Hibernate 6 generates `CALL sp_name(p_name => ?)` syntax. ALL `@Procedure` params need `@Param("p_*")` annotation
- **Ordering**: `DatabaseInitializer @Order(1)` → `UserSeeder @Order(2)`
- **CORS**: `WebConfig.java` defines `CorsConfigurationSource` bean; `SecurityConfig.java` calls `.cors(cors -> {})`
- **Email**: `EmailService` uses `JavaMailSender` with Gmail SMTP app password (from `MAIL_PASSWORD` in `.env`). Tokens expire in 30 minutes. Unknown emails return generic success (no leak)

### Architecture
- **Passive AuthProvider** — only holds user state; all API logic in React Query hooks (`useLogin`, `useForgotPassword`, etc.)
- **Feature structure** — each feature in `features/<name>/` with own pages, hooks, api, schemas, components subdirectories
- **api-client** — fetch wrapper that auto-refreshes tokens on 401 (only when a token exists), throws `ApiError` on non-2xx

---

## Environment

| Service | URL | Notes |
|---------|-----|-------|
| Backend | `http://localhost:5000` | Spring Boot 3.4.4, Java 21 |
| Frontend | `http://localhost:5174` | Vite dev server |
| Database | PostgreSQL on Supabase | Connection in `.env` |
| Email | Gmail SMTP | App password in `.env` (`MAIL_PASSWORD`) |

---

## Useful Commands

```bash
# Backend
cd backend && mvn compile          # compile only
cd backend && mvn spring-boot:run  # run server

# Frontend
cd frontend && npm run dev         # dev server
cd frontend && npm run build       # production build
cd frontend && npm run build       # also runs tsc type-check
```

---

## Current File Structure (Key Files)

### Backend
- `auth/service/AuthService.java` — core auth logic (login, register, forgot/reset password, refresh)
- `common/service/EmailService.java` — sends styled HTML reset-password emails
- `common/config/WebConfig.java` — CorsConfigurationSource bean
- `common/config/SecurityConfig.java` — SecurityFilterChain with `.cors()`
- `common/config/AdminProperties.java` — admin credentials from env
- `common/security/JwtTokenProvider.java` — JWT generation/validation
- `common/security/JwtAuthenticationFilter.java` — Bearer token filter
- `common/exception/GlobalExceptionHandler.java` — centralized error handling

### Frontend
- `lib/api-client.ts` — fetch wrapper, token refresh, throws ApiError
- `lib/api-error.ts` — ApiError class with `errors[]` + `statusCode`
- `lib/error-utils.ts` — `mapErrorToField()` regex matcher
- `lib/password-strength.ts` — 5 password rules definitions
- `components/ui/FloatingLabelInput.tsx` — reusable floating label input
- `components/ui/PasswordInput.tsx` — floating label + eye toggle + strength indicator
- `components/ui/PasswordStrength.tsx` — checklist + progressive bar component
- `features/auth/pages/LoginPage.tsx` — login form with error handling
- `features/auth/pages/ForgotPasswordPage.tsx` — forgot password form
- `features/auth/pages/ResetPasswordPage.tsx` — reset password with token
- `features/auth/schemas/auth.schema.ts` — all yup validation schemas
- `features/auth/hooks/useLogin.ts` — login mutation
- `features/auth/hooks/useForgotPassword.ts` — forgot password mutation
- `features/auth/hooks/useResetPassword.ts` — reset password mutation
- `main.tsx` — Toaster configuration (bottom-left)
- `index.css` — autofill override, custom animations, CSS variables
