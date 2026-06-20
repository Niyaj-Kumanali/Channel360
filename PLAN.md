# Channel360 — Build Plan

## ⚠️ Phase 1 is FROZEN

Do **not** modify any Phase 1 (Auth) code in future phases. Build new features on top of it without altering it. See `instructions.md` for the complete list of frozen files.

## Phase Overview

| Phase | Status |
|-------|--------|
| **1. Auth Foundation** | ✅ **FROZEN** |
| **2. Homepage CMS + Theme** | 🔜 In Progress |
| 3. Channel Operations (Entry, Transfer, Purchase) | 📋 Planned |
| 4. Product Activation + Claims | 📋 Planned |
| 5. External Data + Reporting | 📋 Planned |

## Phase 1 — Auth Foundation ✅

Login, Forgot/Reset Password, Password Strength UI, User & Role Management (CRUD), JWT auth with RBAC, Email Service for password resets.

**→ FROZEN. No modifications allowed.**

## Phase 2 — Homepage CMS + Theme 🔜

### Scope

| Feature | Status |
|---------|--------|
| Landing page (hardcoded, CMS-ready) | ✅ Done |
| Light/Dark theme with persistent toggle | ✅ Done |
| Theme toggle in homepage navbar | ✅ Done |
| Homepage CMS backend (hero banners, announcements, info blocks, promotions, image cards, rich content) | 🔜 Next |
| CMS admin pages (section CRUD behind DashboardLayout) | 🔜 Next |
| Popup management (create, configure, enable/disable with display periods) | 🔜 Next |
| Replace hardcoded homepage sections with CMS-driven dynamic content | 🔜 Next |

### Steps

1. ✅ Feature reorganization (DashboardPage → `features/dashboard/`, HomePage → `features/home/`)
2. ⏳ DashboardLayout (Navbar + Sidebar + Outlet) — needed for CMS admin pages
3. ⏳ CMS backend — `homepage_sections` table, stored procedures, CRUD + public GET endpoints
4. ⏳ CMS frontend admin — section list + create/edit forms
5. ⏳ Popup management backend + frontend
6. ⏳ Swap hardcoded HomePage sections for dynamic CMS render

## Phase 3 — Channel Operations 📋

- Channel Entry: track manufacturer → distributor → partner movement
- Partner Transfer: track inter-distributor movement
- Customer Purchase: end-customer sales tracking
- Channel-specific data tables, CRUD, stored procedures

## Phase 4 — Product Activation + Claims 📋

- Product Activation: link activation records with channel movement
- Claims Management: channel claims and incentive program tracking
- Lifecycle visibility dashboard

## Phase 5 — External Data + Reporting 📋

- External data upload and management
- Reporting dashboards
- Analytics and business intelligence
- Notifications and alerts

## Design Principles

See `instructions.md` for the full list of design principles (SOLID, KISS, DRY, YAGNI, etc.).
