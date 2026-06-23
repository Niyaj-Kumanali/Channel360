# Channel360 — Comprehensive Permissions Catalog

All permissions use `module.action` format. New permissions are configurable via database — no code deployment required.

---

## Platform Management

| Permission | Module | Description |
|------------|--------|-------------|
| `users.view` | users | View user list and details |
| `users.create` | users | Create new users |
| `users.edit` | users | Edit existing users |
| `users.delete` | users | Delete/deactivate users |
| `roles.view` | roles | View role list and details |
| `roles.create` | roles | Create new roles |
| `roles.edit` | roles | Edit existing roles |
| `roles.delete` | roles | Delete roles |
| `permissions.assign` | permissions | Assign permissions to roles |
| `menus.configure` | menus | Configure sidebar menu items |

---

## CMS Administration

| Permission | Module | Description |
|------------|--------|-------------|
| `sections.view` | sections | View homepage sections |
| `sections.create` | sections | Create homepage sections |
| `sections.edit` | sections | Edit homepage sections |
| `sections.delete` | sections | Delete homepage sections |
| `popups.view` | popups | View popups |
| `popups.create` | popups | Create popups |
| `popups.edit` | popups | Edit popups |
| `popups.delete` | popups | Delete popups |

---

## Geographic Scopes

| Permission | Module | Description |
|------------|--------|-------------|
| `regions.view` | regions | View region hierarchy |
| `regions.create` | regions | Create regions |
| `regions.edit` | regions | Edit regions |
| `regions.delete` | regions | Delete regions |

---

## Approval Workflows

| Permission | Module | Description |
|------------|--------|-------------|
| `workflows.view` | workflows | View approval workflows |
| `workflows.configure` | workflows | Configure approval workflow steps |

---

## Access Control

| Permission | Module | Description |
|------------|--------|-------------|
| `access.view` | access | View access requests and grants |
| `access.approve` | access | Approve/reject access requests |
| `access.grant` | access | Grant direct access (ADMIN bypass) |

---

## Operational Areas

| Permission | Module | Description |
|------------|--------|-------------|
| `sales.view` | sales | View daily sales data |
| `sales.upload` | sales | Upload daily sales invoices |
| `sales.approve` | sales | Approve batch corrections |
| `entries.view` | entries | View channel entries |
| `entries.create` | entries | Create channel entries |
| `transfers.view` | transfers | View partner transfers |
| `transfers.create` | transfers | Create partner transfers |
| `activations.view` | activations | View product activations |
| `activations.create` | activations | Create product activations |

---

## Claims

| Permission | Module | Description |
|------------|--------|-------------|
| `claims.view` | claims | View claims |
| `claims.create` | claims | Submit claims |
| `claims.process` | claims | Process/approve claims |

---

## Master Data

| Permission | Module | Description |
|------------|--------|-------------|
| `cities.view` | cities | View city master |
| `cities.create` | cities | Create city records |
| `cities.edit` | cities | Edit city records |
| `cities.import` | cities | CSV import city data |
| `products.view` | products | View product master |
| `products.create` | products | Create product records |
| `products.edit` | products | Edit product records |
| `products.import` | products | CSV import product data |
| `partners.view` | partners | View partner records |
| `partners.create` | partners | Create partner records |
| `partners.edit` | partners | Edit partner records |
| `customers.view` | customers | View customer records |
| `customers.create` | customers | Create customer records |
| `customers.edit` | customers | Edit customer records |

---

## Reporting

| Permission | Module | Description |
|------------|--------|-------------|
| `reports.view` | reports | View standard reports |
| `reports.create` | reports | Create custom reports |
| `analytics.view` | analytics | View analytics dashboards |
| `data.upload` | data | Upload external data |
