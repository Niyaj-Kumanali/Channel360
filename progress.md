> ⚠️ **Phase 1 is FROZEN.** Do not modify any auth-related code.
> See `instructions.md` for the complete list of frozen files.

# Channel360 — Progress

## Phase 1 — Foundation (✅ Completed)

### Backend

- [x] Project setup: Spring Boot 3, Java 21, Maven
- [x] Security: JWT auth, refresh tokens, BCrypt, SecurityFilterChain
- [x] Auth endpoints: Login, Logout, Register, Refresh, Change/Forgot/Reset Password
- [x] User CRUD: Create, Update, Delete, Get, List (paginated + filtered)
- [x] Role CRUD: Create, Update, Delete, Get, List
- [x] User activation/deactivation
- [x] Role assignment to users
- [x] Stored procedures (`sp_user_save`, `sp_role_save`, `sp_user_list`, `sp_user_count`, etc.)
- [x] DatabaseInitializer runs `db/procedures/*.sql` on startup (`@Order(1)`)
- [x] UserSeeder creates default admin on first run (`@Order(2)`)
- [x] Dynamic role-based menu (MenuService + MenuItem DTO)
- [x] GlobalExceptionHandler → ErrorResponse
- [x] DTO reorganization: `request/` and `response/` packages per module
- [x] Three response DTOs: `ApiResponse<T>`, `PageResponse<T>`, `ErrorResponse`
- [x] CORS fix: `.cors()` in SecurityConfig + CorsConfigurationSource bean
- [x] `spring-boot-starter-mail` added to pom.xml
- [x] EmailService with JavaMailSender + styled HTML reset template
- [x] Forgot password sends real email with reset link via Gmail SMTP
- [x] Reset tokens have 30-minute expiry
- [x] No email existence leak (always returns generic success)

### Frontend

- [x] Project setup: React 19, Vite, TypeScript, Tailwind, shadcn/ui
- [x] api-client with auto token refresh + throw on non-2xx (ApiError class)
- [x] AuthProvider as passive state container (no API logic)
- [x] LoginPage → useLogin mutation
- [x] ForgotPasswordPage → useForgotPassword mutation
- [x] ResetPasswordPage → useResetPassword mutation
- [x] UI primitives: Button, Input, Card, FloatingLabelInput, PasswordInput
- [x] Feature structure (`features/auth/`, `components/ui/`, `lib/`)
- [x] Token verification on startup (AuthProvider calls `/auth/me`)
- [x] Conditional routing (authenticated → HomePage, guest → auth pages)
- [x] HomePage with user info + logout button
- [x] Split-screen AuthLayout (amber brand panel left + form panel right)
- [x] Floating label pattern on all inputs (peer CSS technique)
- [x] yup validation (v1) replacing zod
- [x] Field-level error mapping from backend (`mapErrorToField` + `setError`)
- [x] Top-level backend errors shown as toast snackbar
- [x] Toast styled snackbar (bottom-left, white card, amber/red left border)
- [x] Password strength checklist (5 criteria, real-time, left-to-right bar fill)
- [x] Required fields indicated by red asterisk + red border (no text errors)
- [x] Browser native validation suppressed (noValidate on all forms)
- [x] Autofill blue background removed (CSS -webkit-autofill override)
- [x] Validation fires only onBlur (not on every keystroke)

---

## Phase 1 — 🔲 Pending (Known Gaps)

### Backend
- [ ] Audit fields (`created_by`, `updated_by`) populated automatically
- [ ] Input sanitization / validation hardening

### Frontend — User Management Pages
- [ ] UserListPage (DataTable with search, filter, pagination, status toggle, delete)
- [ ] UserCreatePage (form with role checkbox multi-select)
- [ ] UserEditPage (pre-populated form with role checkboxes)
- [ ] RoleListPage (DataTable + create/edit/delete)

### Frontend — Dashboard / Layout
- [ ] DashboardPage (stats cards, recent activity, welcome)
- [ ] Navbar component
- [ ] Sidebar component
- [ ] DashboardLayout (sidebar, header, responsive)

### Frontend — Shared Components
- [ ] DataTable component (reusable, sortable, paginated)
- [ ] Modal component (confirmation dialogs)
- [ ] Badge component (status, role tags)
- [ ] Select component (dropdowns)
- [ ] Loading skeleton components
- [ ] Empty state component
