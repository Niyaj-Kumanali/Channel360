INSERT INTO roles (name, description)
VALUES ('ROLE_ADMIN', 'Administrator with full access')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description)
VALUES ('ROLE_USER', 'Regular user with basic access')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description)
VALUES ('ROLE_SUPER_ADMIN', 'Super administrator with full system access')
ON CONFLICT (name) DO NOTHING;

-- Permissions
INSERT INTO permissions (name, description, module)
VALUES ('dashboard.view', 'View dashboard', 'dashboard')
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (name, description, module)
VALUES ('users.manage', 'Manage users (CRUD)', 'users')
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (name, description, module)
VALUES ('roles.manage', 'Manage roles and permissions', 'roles')
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (name, description, module)
VALUES ('homepage.manage', 'Manage homepage sections and popups', 'cms')
ON CONFLICT (name) DO NOTHING;

-- ROLE_SUPER_ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_SUPER_ADMIN'
ON CONFLICT DO NOTHING;

-- ROLE_ADMIN gets all except roles.manage
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN'
  AND p.name IN ('dashboard.view', 'users.manage', 'homepage.manage')
ON CONFLICT DO NOTHING;

-- ROLE_USER gets dashboard.view only
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_USER'
  AND p.name = 'dashboard.view'
ON CONFLICT DO NOTHING;
