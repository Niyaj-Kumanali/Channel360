# Channel360 — Build Plan

## Overall Status

| Phase | Status |
|-------|--------|
| **1. Foundation** (Auth + User Management) | **In Progress** |
| 2. CMS Builder | Pending |
| 3. Popup Management | Pending |
| 4. Reporting & Analytics | Pending |
| 5. Notifications | Pending |

---

## Phase 1 — Foundation

### ✅ Done

#### Backend
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

#### Frontend
- [x] Project setup: React 19, Vite, TypeScript, Tailwind, shadcn/ui
- [x] api-client with auto token refresh + throw on non-2xx
- [x] AuthProvider as passive state container (no API logic)
- [x] LoginPage → useLogin mutation
- [x] ForgotPasswordPage → useForgotPassword mutation
- [x] ResetPasswordPage → useResetPassword mutation
- [x] UI primitives: Button, Input, Card
- [x] Feature structure (`features/auth/`, `components/ui/`, `lib/`)
- [x] Token verification on startup (AuthProvider calls `/auth/me`)
- [x] Conditional routing (authenticated → HomePage, guest → auth pages)
- [x] HomePage with user info + logout button

### 🔲 Pending

#### Frontend — User Management
- [ ] UserListPage (DataTable with search, filter, pagination, status toggle, delete)
- [ ] UserCreatePage (form with role checkbox multi-select)
- [ ] UserEditPage (pre-populated form with role checkboxes)
- [ ] RoleListPage (DataTable + create/edit/delete)

#### Frontend — Dashboard
- [ ] DashboardPage (stats cards, recent activity, welcome)
- [ ] DashboardLayout (sidebar, header, responsive)

#### Frontend — Shared
- [ ] DataTable component (reusable, sortable, paginated)
- [ ] Modal component (confirmation dialogs)
- [ ] Badge component (status, role tags)
- [ ] Select component (dropdowns)
- [ ] Loading skeleton components
- [ ] Empty state component

#### Backend — Polish
- [ ] Audit fields (`created_by`, `updated_by`) populated automatically
- [ ] Input sanitization / validation hardening

---

## Phase 2 — CMS Builder

- [ ] Database tables: `homepage_sections`
- [ ] Backend CRUD for sections (reorder, activate/deactivate, schedule)
- [ ] Frontend CMS management pages
- [ ] Dynamic homepage rendering by display order
- [ ] Section types: Hero Banner, Announcement, Info Block, Rich Text, Image Card, Promotion

---

## Phase 3 — Popup Management

- [ ] Database tables: `popups`
- [ ] Backend CRUD for popups
- [ ] Frontend popup management pages
- [ ] Priority-based display logic
- [ ] Schedule with start/end dates

---

## Phase 4 — Reporting & Analytics

- [ ] User activity tracking
- [ ] CSV/Excel export
- [ ] Charts and dashboards
- [ ] Sell-In / Sell-Thru / Sell-Out reports

---

## Phase 5 — Notifications

- [ ] In-app notification system
- [ ] Email templates
- [ ] Notification preferences per user
- [ ] Real-time via WebSocket

---

## Design Decisions (Why)

| Decision | Rationale |
|----------|-----------|
| Stored procedures for writes | DB-first; business logic in DB; performance; independent of app deploys |
| JPA for simple reads | Less boilerplate than procedures; type-safe; faster dev |
| `void` return from save procedures | Hibernate 6 INOUT bug workaround; fetch by email after save |
| `EntityManager` for REFCURSOR | `@Procedure` can't bind REFCURSOR + named params in Hibernate 6 |
| Feature-based frontend | Scales to 20+ modules; each feature is self-contained; easy to add/remove |
| Providers as passive containers | Keeps API logic in React Query hooks; providers only manage client state |
| api-client throws on non-2xx | Makes `useMutation.onError` work correctly; no caller checks `response.success` |
| `PageResponse<T>` unwrapped | Frontend accesses `data.content` directly, not `data.data.content` |
