# Channel360 — Multi-Domain Enterprise Architecture Diagram

This document delineates the overarching system architecture topology and cross-cutting components governing Channel360.

---

## 1. System Topology Layout
 +-------------------------------------------------------+
 |                 Frontend Client Layer                 |
 |           React 19 SPA (Vite, Tailwind CSS)           |
 +---------------------------+---------------------------+
                             |
                    HTTPS (JSON / JWT)
                             |
                             v
 +-------------------------------------------------------+
 |              Spring Boot 3 REST API Layer             |
 |  +-------------------------------------------------+  |
 |  | Security Filter Chain (Stateless JWT Validator) |  |
 |  +------------------------+------------------------+  |
 |                           |                           |
 |                           v                           |
 |  +-------------------------------------------------+  |
 |  | Controller Layer (ApiResponse / PageResponse)   |  |
 |  +------------------------+------------------------+  |
 |                           |                           |
 |                           v                           |
 |  +-------------------------------------------------+  |
 |  | Service Domain Layer (Business Rules & Engine)  |  |
 |  +------------------------+------------------------+  |
 |                           |                           |
 |                           v                           |
 |  +-------------------------------------------------+  |
 |  | Repository Data Abstraction Layer (Spring Data) |  |
 |  +-------------------------------------------------+  |
 +---------------------------+---------------------------+
                             |
                   JPA / Stored Procedures
                             |
                             v
 +-------------------------------------------------------+
 |                PostgreSQL Storage Layer               |
 |  +-------------------------------------------------+  |
 |  | RBAC Tables (Roles, Permissions, Grants)        |  |
 |  +-------------------------------------------------+  |
 |  | Hierarchy Trees (Self-Referencing Regions)       |  |
 |  +-------------------------------------------------+  |
 |  | Transaction Processing & Stored Procedures      |  |
 |  +-------------------------------------------------+  |
 +-------------------------------------------------------+

---

## 2. Shared Boundary Definitions
* **State Management Isolation:** React components remain layout wrappers. Global client state caching is strictly isolated to TanStack Query caches.
* **Hybrid Database Paradigm:** Data persistence maps standard relations through JPA entities for lightweight object operations. Deep data validation orchestration, multi-row calculation transformations, and high-performance pipeline entries are assigned to database-level PostgreSQL stored procedures (`sp_*`).
* **Audit Trail Integration:** Every modification engine relies on an aspect or interceptor hooking database changes into an `audit_logs` record containing transaction structural differences using a JSONB metadata schema layout.
