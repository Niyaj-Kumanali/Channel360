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

## Phase 2 — Platform

### Step 1: Feature Reorganization (✅ Done)
- [x] Move `DashboardPage` → `features/dashboard/pages/DashboardPage.tsx`
- [x] Move `HomePage` → `features/home/pages/HomePage.tsx`
- [x] Update `AppRouter.tsx` imports
- [x] Delete old files from `features/auth/pages/`
- [x] Auth storage switched from `localStorage` to `sessionStorage` (via `lib/storage.ts`)
- [x] `appStorage` wrapper for `localStorage` ready for theme/settings

### Step 2: DashboardLayout
- [ ] Navbar component
- [ ] Sidebar component
- [ ] DashboardLayout (Navbar + Sidebar + `<Outlet />`, responsive)
- [ ] Wire into authenticated routes in AppRouter

### Step 3: CMS Backend
- [ ] `homepage_sections` table + stored procedures
- [ ] CRUD endpoints (admin)
- [ ] Public GET endpoint (published sections only)
- [ ] Menu entries for CMS admin pages

### Step 4: CMS Frontend Admin
- [ ] Section list page (behind DashboardLayout)
- [ ] Section create/edit forms

### Step 4b: Dark/Light Theme (✅ Done)
- [x] `.dark` CSS variables added to `index.css` (additive, `:root` untouched)
- [x] `ThemeProvider` with `appStorage` (localStorage) persistence + system preference detection
- [x] `useTheme` hook: `{ theme, toggleTheme, setTheme }`
- [x] Theme toggle button in HomePage navbar (Sun/Moon icons)
- [x] Toaster, autofill CSS adapt to active theme
- [x] HomePage uses theme-aware classes (`bg-background`, `text-foreground`, `border-border`, `bg-card`, etc.)
- [x] Uses existing Phase 1 `Logo` component with theme-based variant switching

### Step 5: Public Homepage (✅ Hardcoded — CMS pending)
- [x] Navbar: Logo component + theme toggle + Sign In
- [x] Hero: "Complete Visibility Across Your Channel Ecosystem" — positioned as enterprise channel platform
- [x] Stats bar: End-to-End Lifecycle, Multi-Tier Support, Role-Based Access, CMS-Driven Content
- [x] Product Journey: 5-step flow (Manufacturer → Distributor → Channel Partner → End Customer → Activation)
- [x] — Bouncing ball travels between 5 nodes (bounce-h + bounce-v, 7s, 20px parabolic bounces)
- [x] — On return to Manufacturer, ball jumps into gear/recycle icon (ball-repair keyframe, -51px)
- [x] — Gear/recycle rises first, ball follows; gear spins 1080°; ball drops first, gear descends
- [x] — Alternating gear/recycle icons per cycle (gearTypeRef + 4900ms/7000ms timeouts)
- [x] — JS-controlled timing (no onAnimationEnd) for precise sync across all animations
- [x] Business Areas: 2D radial network diagram with concentric orbit rings, spokes, curved arcs, cross-connections, single amber color
- [x] Benefits section: 5 key value propositions with bullet styling
- [x] CTA section + Footer with Logo component
- [x] Hero background: stylized 3D amber wireframe globe centered with accurate continent shapes (Natural Earth GeoJSON), 7 city markers, curved connection arcs, animated jumping dots radiating from India (Mumbai)
- [x] All content reflects actual Channel360 platform purpose (not generic marketing SaaS)
- [ ] Replace with CMS-driven dynamic sections (when CMS backend is built)

### Pending (Known Gaps)
- [ ] Audit fields (`created_by`, `updated_by`) populated automatically
- [ ] Input sanitization / validation hardening
- [ ] User management CRUD pages
- [ ] Role management CRUD pages
- [ ] Shared components: DataTable, Modal, Badge, Select, Skeleton, EmptyState
