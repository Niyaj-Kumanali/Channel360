# Channel360 — Build Plan

## Phase Overview

| Phase | Status |
|-------|--------|
| **1. Auth Foundation** | ✅ **FROZEN** |
| **2. Homepage CMS + Theme** | ✅ **FROZEN** |
| 3. Channel Operations (Entry, Transfer, Purchase) | 📋 Planned |
| 4. Product Activation + Claims | 📋 Planned |
| 5. External Data + Reporting | 📋 Planned |

## Phase 1 — Auth Foundation ✅

Login, Forgot/Reset Password, Password Strength UI, User & Role Management (CRUD), JWT auth with RBAC, Email Service for password resets.

**→ FROZEN. No modifications allowed.**

## Phase 2 — Homepage CMS + Theme ✅

### Scope

| Feature | Status |
|---------|--------|
| Landing page (staticSections + API-driven fallback) | ✅ Done |
| 6 core sections + 5 optional CMS-managed sections | ✅ Done |
| Light/Dark theme with persistent toggle | ✅ Done |
| Theme toggle in homepage navbar | ✅ Done |
| Homepage CMS backend (sections CRUD, popups, stored procedures) | ✅ Done |
| CMS admin pages (section list with DnD reorder, create/edit with live preview, toggle) | ✅ Done |
| Popup management (create, configure, enable/disable with display periods) | ✅ Done |
| FAQ page at `/faq` | ✅ Done |
| Section type validation in backend DTO | ✅ Done |
| Custom easings in Tailwind config | ✅ Done |
| All content reflects actual Channel360 platform purpose | ✅ Done |

**→ FROZEN. No modifications allowed.**

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
