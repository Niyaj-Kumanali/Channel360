# Channel360 – Modular Monolith Architecture Rules

## Purpose

Channel360 is currently being built as a Modular Monolith.

The goal is:

- Fast development
- Simpler deployment
- Lower infrastructure cost
- Easier debugging

while keeping a future migration path to Microservices.

The architecture must therefore be:

> Microservice Ready Modular Monolith

This means modules remain physically inside one application but are logically isolated.

---

# Core Principle

Bad:

Monolith Today
→ Rewrite Later

Good:

Modular Monolith Today
→ Extract Modules Tomorrow

Future migration should be evolutionary, not a rewrite.

---

# Architecture Goal

The architecture should support future extraction into:

- Identity Service
- Organization Service
- Workflow Service
- Master Data Service
- Channel Operations Service
- Reporting Service

without major code rewrites.

---

# Current Module Structure

```text
com.channel360

├── auth
├── users
├── roles
├── permissions
├── menus
├── cms
├── popup
├── regions
├── workflows
├── access
├── common
└── Channel360Application
```

Each module owns:

```text
module

├── api
├── application
├── domain
├── infrastructure
```

Example:

```text
roles

├── api
├── application
├── domain
└── infrastructure
```

---

# Layer Responsibilities

## api

Purpose:

Expose functionality to other modules.

Contains:

- Facades
- Public DTOs
- Public Contracts

Example:

```java
public interface RoleFacade {

    RoleDto findById(UUID id);

    List<RoleDto> getUserRoles(UUID userId);
}
```

Only api package is visible to other modules.

---

## application

Purpose:

Business use cases.

Contains:

- Services
- Use Cases
- Commands
- Queries

Example:

```text
CreateUserService
AssignRoleService
ApproveRequestService
```

---

## domain

Purpose:

Business model.

Contains:

- Entities
- Domain Rules
- Domain Services
- Value Objects

---

## infrastructure

Purpose:

Technical implementation.

Contains:

- JPA Repositories
- Stored Procedure Calls
- External Integrations
- Persistence Adapters

---

# Critical Rule #1

Never Import Another Module Repository

Forbidden:

```java
@Autowired
private RoleRepository roleRepository;
```

inside User module.

Forbidden:

```java
@Autowired
private RegionRepository regionRepository;
```

inside Workflow module.

Repositories are internal implementation details.

---

# Correct Pattern

Use Facades.

Example:

```java
@RequiredArgsConstructor
public class UserService {

    private final RoleFacade roleFacade;

}
```

Allowed:

```java
user
   ↓
role.api
```

Not:

```java
user
   ↓
role.repository
```

---

# Critical Rule #2

Never Share Entities Across Modules

Forbidden:

```java
Role role = roleRepository.findById(id);
```

outside Role module.

Forbidden:

```java
User user = userRepository.findById(id);
```

outside User module.

---

# Correct Pattern

Expose DTOs.

Example:

```java
RoleSummaryDto
```

```java
RoleResponseDto
```

```java
UserSummaryDto
```

Only DTOs cross module boundaries.

---

# Critical Rule #3

No Cross Module JPA Relationships

Forbidden:

```java
@ManyToOne
private Role role;
```

inside User entity.

Forbidden:

```java
@OneToMany
private List<User> users;
```

inside Role entity.

This creates tight coupling.

---

# Correct Pattern

Store IDs.

Example:

```java
private UUID roleId;
```

```java
private UUID regionId;
```

```java
private UUID workflowId;
```

Resolve through Facades.

Example:

```java
RoleDto role =
    roleFacade.findById(user.getRoleId());
```

---

# Critical Rule #4

No Circular Dependencies

Forbidden:

```text
User
 ↓
Role

Role
 ↓
User
```

Forbidden:

```text
Workflow
 ↓
Region

Region
 ↓
Workflow
```

Circular dependencies block extraction.

---

# Correct Pattern

One Direction Only

Example:

```text
Workflow
 ↓
Region API
```

Region never depends on Workflow.

---

# Critical Rule #5

Use Events For Side Effects

Avoid:

```java
createUser();

sendEmail();

createAudit();

createNotification();
```

inside one service.

Prefer:

```java
UserCreatedEvent
```

Listeners:

```java
AuditListener

NotificationListener

EmailListener
```

Current implementation may remain synchronous.

Future microservices can consume same events.

---

# Critical Rule #6

Database Ownership

Every table has one owner module.

Example:

users
→ User Module

roles
→ Role Module

regions
→ Region Module

approval_workflows
→ Workflow Module

Only owning module may modify table.

Other modules use APIs.

---

# Critical Rule #7

No Shared Business Logic

Forbidden:

```java
common/UserUtil.java
common/WorkflowUtil.java
common/RoleUtil.java
```

Business logic belongs inside owning module.

Common package should contain only:

- Config
- Security
- Exceptions
- Response Objects
- Constants
- Utilities

Never business rules.

---

# Future Microservice Targets

## Identity Service

Modules:

- auth
- users
- roles
- permissions
- menus

---

## Organization Service

Modules:

- regions
- region approvers

---

## Workflow Service

Modules:

- workflows
- access requests
- access grants

---

## Master Data Service

Modules:

- city master
- product master
- partner master
- customer master

---

## Channel Operations Service

Modules:

- daily sales
- channel entries
- transfers
- activations
- claims

---

## Reporting Service

Modules:

- reports
- analytics
- dashboards

---

# Design Checklist

Before creating any module verify:

✓ Does this module own its tables?

✓ Does it expose an API layer?

✓ Are repositories hidden?

✓ Are entities hidden?

✓ Are only DTOs crossing boundaries?

✓ Are dependencies one-directional?

✓ Can this module be extracted later?

If any answer is No, redesign before implementation.

---

# Golden Rule

Build:

Microservice Ready Modular Monolith

Do not build:

Monolith That Requires Rewrite


# Critical Rule #8

Never inject repositories from another module even "just for a lookup".

If data from another module is needed:

Module -> Facade -> DTO

Always.

Even if it feels slower to implement.