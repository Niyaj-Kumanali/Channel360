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

## Phase 2 — Homepage CMS + Theme (✅ Completed)

### Feature Reorganization
- [x] Move `DashboardPage` → `features/dashboard/pages/DashboardPage.tsx`
- [x] Move `HomePage` → `features/home/pages/HomePage.tsx`
- [x] Update `AppRouter.tsx` imports
- [x] Delete old files from `features/auth/pages/`
- [x] Auth storage switched from `localStorage` to `sessionStorage` (via `lib/storage.ts`)
- [x] `appStorage` wrapper for `localStorage` ready for theme/settings

### DashboardLayout
- [x] Navbar component
- [x] Sidebar component
- [x] DashboardLayout (Navbar + Sidebar + `<Outlet />`, responsive)
- [x] Wired into authenticated routes in AppRouter

### CMS Backend
- [x] `homepage_sections` table + stored procedures (`sp_homepage_section_save`, `sp_homepage_section_delete`, `sp_homepage_section_reorder`)
- [x] CRUD endpoints (admin): create, update, delete, get all, reorder
- [x] Public GET endpoint (published sections only, filtered by active + date range)
- [x] Menu entries for CMS admin pages
- [x] `homepage_popups` table + CRUD + public GET endpoint
- [x] Section type validation via `@Pattern` regex + `AppConstants.VALID_SECTION_TYPES`
- [x] Seed data: 11 section rows (6 active, 5 inactive for super admin to enable)

### CMS Frontend Admin
- [x] Section list page (behind DashboardLayout) with DnD reorder
- [x] Section create/edit slide-over panel with live preview
- [x] Toggle active/inactive per section
- [x] Popup list page + create/edit form
- [x] SECTION_TYPES enum shared across CMS and homepage

### Popup Management
- [x] PopupModal component with timed dismissal
- [x] Priority-based sorting, active date range filtering
- [x] Static fallback + API-driven

### Dark/Light Theme (✅ Done)
- [x] `.dark` CSS variables added to `index.css` (additive, `:root` untouched)
- [x] `ThemeProvider` with `appStorage` (localStorage) persistence + system preference detection
- [x] `useTheme` hook: `{ theme, toggleTheme, setTheme }`
- [x] Theme toggle button in HomePage navbar (Sun/Moon icons)
- [x] Toaster, autofill CSS adapt to active theme
- [x] HomePage uses theme-aware classes (`bg-background`, `text-foreground`, `border-border`, `bg-card`, etc.)
- [x] Uses existing Phase 1 `Logo` component with theme-based variant switching

### Public Homepage (CMS-Driven + Static Fallback)

**Core Sections (6 active by default):**
- [x] Hero: RotatingEarth 3D globe background, gradient overlay, CTA button
- [x] Product Journey: 5-step pipeline with bouncing ball animation, departure-based highlight + pulse, mobile arrows
- [x] Platform Capabilities: 6 unique inline SVG visualizations, masonry layout, staggered entry
- [x] Benefits: 5 inline SVG components (TimelineViz, HubViz, WaveViz, ShieldViz, GrowthViz), gradient heading, staggered reveal
- [x] Contact: Enterprise split layout, 4 floating gradient blobs, unified accent, multi-color title
- [x] Footer: CMS-backed multi-column layout, `useTheme` for logo, parses description JSON

**Optional Sections (5 inactive, super admin enables via CMS):**
- [x] Announcement: MegaphoneViz inline SVG, gradient heading, CTA
- [x] Information Block: InfoViz inline SVG, 2-col grid, CTA
- [x] Promotion: BadgeViz inline SVG, badge pill, gradient heading, CTA
- [x] Image Card: ImagePlaceholderViz inline SVG, card with image area
- [x] FAQ: DocumentViz inline SVG, dual-mode (section shows 2 Q&A + "View All FAQs" → full page at `/faq`)

**Homepage Architecture:**
- [x] Static sections render immediately (no loader), API replaces content on arrival
- [x] All sections `min-h-[calc(100vh-4rem)]` at all screen sizes
- [x] SectionRenderer maps sectionType → component
- [x] Consistent styling: gradient headings, inline SVGs, theme-aware classes
- [x] `business_areas` and `stats_bar` removed (obsoleted by CMS-driven approach)

### FAQ Page
- [x] Dedicated `/faq` route outside auth ternary (accessible to all)
- [x] FaqPage fetches from CMS API with static fallback
- [x] "Back to Home" navigation
- [x] `faq` section type fully registered in SECTION_TYPES, icons, renderers, seed

### Polish (Low Priority)
- [x] Tailwind `ease-[cubic-bezier(...)]` warnings resolved — custom easings in `tailwind.config.js`
- [x] Backend section type validation — `@Pattern` regex on `HomepageSectionRequest.sectionType`
- [x] Section type constants in `AppConstants.java` with `VALID_SECTION_TYPES` array

---

## Phase 3 — Channel Operations 📋 Planned

- Channel Entry: track manufacturer → distributor → partner movement
- Partner Transfer: track inter-distributor movement
- Customer Purchase: end-customer sales tracking
- Channel-specific data tables, CRUD, stored procedures

## Phase 4 — Product Activation + Claims 📋 Planned

- Product Activation: link activation records with channel movement
- Claims Management: channel claims and incentive program tracking
- Lifecycle visibility dashboard

## Phase 5 — External Data + Reporting 📋 Planned

- External data upload and management
- Reporting dashboards
- Analytics and business intelligence
- Notifications and alerts
