
# Channel360 — Architecture Plan

## Core Architecture Decisions

### 1. Separate Identity From User
- **Auth module owns:** `auth_users`, `refresh_tokens`, `password_reset_tokens`, login, JWT, forgot/reset password
- **Users module owns:** employee profile (employee_id, mobile, region assignment, status) — business data only
- This enables LDAP/Azure AD/SSO/Google Login later without touching user business logic

### 2. Facades as Interfaces (not just classes)
Each module exposes an interface facade + DTOs:
```java
public interface RoleFacade {
    RoleDto findById(Long id);
    List<RoleDto> getRolesByUser(Long userId);
    // ...
}
```
Consumer modules inject `private final RoleFacade roleFacade` — never repositories from other modules.

### 3. Domain Events from Day One (Spring Events)
- `UserCreatedEvent`, `RoleAssignedEvent`, `WorkflowApprovedEvent`, etc.
- `@EventListener` today → Kafka/RabbitMQ/SQS tomorrow with same event contract

### 4. Package Structure Per Module
```
roles/
├── api/
│   ├── RoleFacade.java        (interface)
│   └── dto/                   (RoleDto, etc.)
├── application/
│   ├── service/               (orchestration)
│   ├── command/               (write use-cases)
│   └── query/                 (read use-cases)
├── domain/
│   ├── entity/
│   ├── event/
│   └── valueobject/
└── infrastructure/
    ├── repository/
    ├── persistence/
    └── mapper/
```

### 5. Module Ownership & Tables
| Module | Owns (tables) | Exposes Facade |
|--------|--------------|----------------|
| auth | `auth_users`, `refresh_tokens`, `password_reset_tokens` | AuthFacade |
| roles | `roles`, `role_permissions` | RoleFacade |
| permissions | `permissions` | PermissionFacade |
| menus | `menu_items`, `menu_role_permissions` | MenuFacade |
| regions | `regions`, `region_approvers`, `user_regions` | RegionFacade |
| users | `users` (business profile only) | UserFacade |
| workflows | `approval_workflows`, `approval_workflow_steps`, `approval_requests`, `approval_tasks` | WorkflowFacade |
| audit | `audit_logs` | AuditFacade |

### 6. Allowed Dependencies (only through Facades)
```
Users → Roles       (via RoleFacade)
Users → Regions     (via RegionFacade)
Workflow → Regions  (via RegionFacade)
Workflow → Users    (via UserFacade)
Menus → Permissions (via PermissionFacade)
Roles → Permissions (via PermissionFacade)
```

### 7. Forbidden Dependencies
```
Users → RoleRepository
Workflow → RegionRepository
Roles → UserRepository
Menus → PermissionRepository
... any cross-module repository/entity import
```

## Implementation Order
- **Phase 3:** Roles → Permissions → Menus → Users → Regions → Region Approvers
- **Phase 4:** Workflow Engine → Access Requests → Access Grants → Audit Logs

**Why this order:** Workflow engine needs RoleFacade + RegionFacade + UserFacade to resolve "which manager for which region". Build region approvers before the workflow engine.

## Progress

### Done
- Modular Monolith restructure (Phase 1-3): all 10 modules restructured with api/application/domain/infrastructure layers
- Facades created: UserFacade, RoleFacade, RegionFacade, RegionApproverFacade, WorkflowFacade, MenuFacade, AuthFacade
- All cross-module repository/entity imports replaced with facade calls
- Build passes `mvn clean compile` on JDK 21 (Lombok 1.18.38 incompatible with JDK 25)

### Next Steps
- Convert facades from classes to interfaces (Rule #2 above)
- Separate Auth tables (auth_users, refresh_tokens, password_reset_tokens) from User tables
- Add domain events (Spring Events)
- Restructure packages to include command/, query/, event/, valueobject/
- Phase 3: Roles → Permissions → Menus → Users → Regions → Region Approvers
- Phase 4: Workflow Engine → Access Requests → Access Grants → Audit Logs
