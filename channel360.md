# Channel360 — Channel Data Management Platform

## Overview

Channel360 is an enterprise-grade channel data management platform with multi-role RBAC (Role-Based Access Control). It provides 360° visibility across distributors, retailers, and partners — enabling organizations to manage their entire channel ecosystem from a single platform.

## Architecture

```
Frontend (React 19 SPA) ⇄ Backend (Spring Boot 3 REST API) ⇄ PostgreSQL
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 19, TypeScript, Vite 6, Tailwind CSS, React Query, React Hook Form, Yup |
| Backend | Spring Boot 3.4.4, Spring Security, Spring Data JPA, Spring Mail, JWT (jjwt), MapStruct, Lombok |
| Database | PostgreSQL with stored procedures |
| Auth | JWT (access + refresh tokens), BCrypt, RBAC |
| Email | Gmail SMTP (forgot/reset password flow) |

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
| 2 | Home Page with CRM dashboard | 🔜 Next |
| TBD | CMS Builder | 📋 Planned |
| TBD | Popup Management | 📋 Planned |
| TBD | Reporting & Analytics | 📋 Planned |
| TBD | Notifications | 📋 Planned |

## Environment

| Service | URL | Notes |
|---------|-----|-------|
| Backend | `http://localhost:5000` | Spring Boot 3.4.4, Java 21 |
| Frontend | `http://localhost:5174` | Vite dev server |
| Database | PostgreSQL on Supabase | Connection in `backend/.env` |
| Email | Gmail SMTP | App password in `backend/.env` |
