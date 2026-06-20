# Channel360 — Channel Management & Intelligence Platform

## Introduction

**Channel360** is a centralized Channel Management and Intelligence Platform designed to provide organizations with complete visibility into their product journey, partner ecosystem, and business operations through a single unified portal.

The platform enables businesses to track and manage the complete lifecycle of products across the distribution network, from initial channel entry to end-customer engagement and product activation. In addition, it provides a secure and configurable environment for user management, content management, announcements, and partner communications.

Channel360 follows a modern CMS-driven architecture that allows administrators to manage content, promotions, and homepage experiences dynamically without requiring code deployments.

## Architecture

```
Frontend (React SPA) ⇄ Backend (Spring Boot 3 REST API) ⇄ PostgreSQL
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 19, TypeScript, Vite 6, Tailwind CSS, TanStack Query, React Hook Form, Yup |
| Backend | Java 21, Spring Boot 3, Spring Security, Spring Data JPA, JWT (jjwt), MapStruct, Lombok |
| Database | PostgreSQL with stored procedures |
| Auth | JWT (access + refresh tokens), BCrypt, RBAC |
| Email | Gmail SMTP (forgot/reset password flow) |

## Core Business Areas

### Channel Entry
Track product movement from manufacturers to distributors and strategic partners.

### Partner Transfer
Track product movement between distributors and channel partners.

### Customer Purchase
Track product sales from channel partners to end customers.

### Product Activation
Provide activation visibility by connecting activation records with channel movement data, enabling complete lifecycle tracking for each product.

### Claims Management
Support management and tracking of channel-related claims and incentive programs.

### External Data Integration
Allow organizations to upload and manage business data received from external sources for reporting and analysis purposes.

## Development Setup

1. Clone the repository
2. **Backend:** `cd backend && mvn spring-boot:run` (uses `.env` for configuration)
3. **Frontend:** `cd frontend && npm run dev` (runs on `http://localhost:5174`)

## Feature Roadmap

| Phase | Feature | Status |
|-------|---------|--------|
| 1 | Auth (Login, Forgot/Reset Password) | ✅ Completed |
| 1 | User & Role Management (CRUD, RBAC) | ✅ Completed |
| 1 | Password strength UI, email notifications | ✅ Completed |
| 2 | Homepage CMS (Hero Banners, Announcements, Info Blocks, Promotions, Image Cards, Rich Content) | 🔜 Next |
| 2 | Popup Management | 🔜 Next |
| 2 | Light/Dark Theme | 🔜 Next |
| TBD | Channel Entry Management | 📋 Planned |
| TBD | Partner Transfer Management | 📋 Planned |
| TBD | Customer Purchase Tracking | 📋 Planned |
| TBD | Product Activation Tracking | 📋 Planned |
| TBD | Claims Management | 📋 Planned |
| TBD | External Data Integration | 📋 Planned |
| TBD | Reporting & Analytics | 📋 Planned |
| TBD | Notifications & Alerts | 📋 Planned |

## Design System

| Token | Value |
|-------|-------|
| Brand color | Amber (`--primary: 38 92% 50%`) |
| App name | Lowercase "channel360" |
| Radius | `0.5rem` |
| Font | Inter |

## Environment

| Service | URL | Notes |
|---------|-----|-------|
| Backend | `http://localhost:5000` | Spring Boot 3, Java 21 |
| Frontend | `http://localhost:5174` | Vite dev server |
| Database | PostgreSQL on Supabase | Connection in `backend/.env` |
| Email | Gmail SMTP | App password in `backend/.env` |
