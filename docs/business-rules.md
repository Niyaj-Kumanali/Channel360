# Channel360 — Core Business Domain & Authorization Matrix

This document defines the rules managing user scopes, geographic scoping, permissions, and multi-tier operation pipelines for Phase 3 through Phase 8.

---

## 1. Core Roles and Business vs. Platform Separation
Channel360 strictly decouples **Platform Administration** from **Business Administration**:

* **SUPER_ADMIN (Platform Owner):** Manages users, roles, system menus, configurations, workflows, and database permission tables. **Cannot** view transactional channel sales data, run corporate claims, or approve business-level records.
* **ADMIN (Highest Business Authority):** Oversees complete operational capabilities, handles global multi-region reports, manages operational escalations, and can override pending approval chains. **Cannot** execute structural platform reconfigurations.
* **MANAGER (Geographically Scoped Business Approver):** Evaluates local business records. Role visibility is structurally derived from assignments within the region hierarchy tree.
  * *Design Constraint:* No individual static roles like `STATE_MANAGER` or `REGIONAL_MANAGER` exist. There is only a single `MANAGER` role whose authority level matches their respective position in the self-referencing region tree.

---

## 2. Dynamic Authorization Architecture
* **No Java Enums for Roles or Permissions:** Roles and granular permissions (`users.create`, `claims.approve`) are explicitly managed using database relational lookup tables (`roles`, `permissions`, `role_permissions`). This allows modifications without software redeployments. Technical transaction indicators (e.g., `Status.ACTIVE`, `RequestStatus.PENDING`) *must* use standard Java Enums.
* **Geographic Hierarchy Trees:** Geographies use a self-referencing hierarchy pattern inside a single table (`regions`). Tree types support distinct B2B and B2C trees spanning Zone $\rightarrow$ Region $\rightarrow$ State $\rightarrow$ Territory. Split tier structures must avoid separate domain tables.

---

## 3. Business Operation Pipeline
[Channel Entry] ----> [Partner Transfer] ----> [Customer Purchase] ----> [Product Activation]
(Mfg -> Dist)          (Dist -> Partner)       (Partner -> Client)      (Warranty/Telemetry)

* **Data Enrichment Engine:** Transactions (such as Daily Sales CSV ingestion) flow through execution pipelines that automatically resolve standard text inputs against the City Master data to assign geographic IDs and link the row to specific parent territories.