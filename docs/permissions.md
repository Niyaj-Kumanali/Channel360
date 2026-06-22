# Channel360 — Comprehensive Permissions Catalog

This document defines the structured permission rules that control application access.

---

## 1. Structural Namespace Schema Blueprint
All permissions must use a strict `module.action` format inside the database:

| Functional Module | Permission Identifiers | Operational Access Description |
| :--- | :--- | :--- |
| **Platform Management** | `users.create`, `users.view`, `users.edit`, `users.delete` | Manages core user identity profiles. |
| | `roles.manage`, `permissions.assign` | Configures dynamic RBAC access structures. |
| | `menus.configure` | Updates sidebar layout endpoints dynamically. |
| **CMS Administration** | `cms.edit`, `popups.manage` | Controls marketing components and priority items. |
| **Geographic Scopes** | `regions.manage` | Modifies the self-referencing hierarchy structure. |
| **Approval Flow Engines**| `workflows.configure` | Modifies multi-tier routing logic patterns. |
| **Operational Areas** | `sales.upload`, `sales.view`, `sales.approve` | Coordinates transaction ingestion pipelines. |
| | `claims.view`, `claims.process` | Evaluates partner claims and incentive programs. |