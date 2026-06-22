# Channel360 — Architecture Decision Records (ADRs)

This document catalogs core architecture decisions tracking critical infrastructure design justifications.

---

## ADR 001: Separation of Platform Administration from Operational Actions
* **Status:** Approved
* **Context:** Early-stage designs often combine administrative actions and transactional reporting paths under a singular administrative identity, exposing operational workflows to configuration errors.
* **Decision:** We strictly decouple `SUPER_ADMIN` platform privileges from operational `ADMIN` views. The `SUPER_ADMIN` acts purely as an infrastructure manager, while business-level permissions require explicit routing via operational rules.
* **Consequences:** This simplifies auditing and ensures that platform configurations do not conflict with day-to-day business data access.

---

## ADR 002: Dynamic Database-Driven RBAC System
* **Status:** Approved
* **Context:** Hardcoded role and permission configurations require compiling and redeploying software artifacts when business rule requirements change.
* **Decision:** Roles, permissions, and menu layout items must live entirely inside dynamic database tables. Software application filters resolve application privileges dynamically against access grant records.
* **Consequences:** System configurations can be updated on the fly without deployment down-time. Enums remain restricted to static technical statuses.