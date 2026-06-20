INSERT INTO roles (name, description)
VALUES ('ROLE_ADMIN', 'Administrator with full access')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description)
VALUES ('ROLE_USER', 'Regular user with basic access')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description)
VALUES ('ROLE_SUPER_ADMIN', 'Super administrator with full system access')
ON CONFLICT (name) DO NOTHING;

-- Remove old coarse permissions (replaced by granular ones below)
DELETE FROM permissions WHERE name IN ('users.manage', 'roles.manage', 'homepage.manage');

-- Permissions (granular: view/create/edit/delete per module)
INSERT INTO permissions (name, description, module)
VALUES ('dashboard.view', 'View dashboard', 'dashboard')
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (name, description, module)
VALUES ('users.view', 'View users', 'users')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('users.create', 'Create users', 'users')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('users.edit', 'Edit users', 'users')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('users.delete', 'Delete users', 'users')
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (name, description, module)
VALUES ('roles.view', 'View roles', 'roles')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('roles.create', 'Create roles', 'roles')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('roles.edit', 'Edit roles', 'roles')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('roles.delete', 'Delete roles', 'roles')
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (name, description, module)
VALUES ('homepage.view', 'View homepage sections and popups', 'cms')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('homepage.create', 'Create homepage sections and popups', 'cms')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('homepage.edit', 'Edit homepage sections and popups', 'cms')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('homepage.delete', 'Delete homepage sections and popups', 'cms')
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (name, description, module)
VALUES ('menu.manage', 'Manage sidebar menu items', 'menu')
ON CONFLICT (name) DO NOTHING;

-- ROLE_SUPER_ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_SUPER_ADMIN'
ON CONFLICT DO NOTHING;

-- ROLE_ADMIN gets dashboard.view, all users.*, all homepage.*
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN'
  AND p.name IN ('dashboard.view',
                 'users.view', 'users.create', 'users.edit')
ON CONFLICT DO NOTHING;

-- ROLE_USER gets dashboard.view only
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_USER'
  AND p.name = 'dashboard.view'
ON CONFLICT DO NOTHING;

-- Menu items (permission-driven, idempotent per-item inserts)
INSERT INTO menu_items (label, path, icon, permission_name, display_order)
SELECT 'Dashboard', '/dashboard', 'LayoutDashboard', 'dashboard.view', 1
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Dashboard' AND parent_id IS NULL);

INSERT INTO menu_items (label, path, icon, permission_name, display_order)
SELECT 'Content', '#', 'FileText', 'homepage.view', 2
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Content' AND parent_id IS NULL);

INSERT INTO menu_items (label, path, icon, permission_name, display_order)
SELECT 'Roles', '/admin/roles', 'Shield', 'roles.view', 3
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Roles' AND parent_id IS NULL);

INSERT INTO menu_items (label, path, icon, permission_name, display_order)
SELECT 'Menu', '/admin/menu', 'Menu', 'menu.manage', 4
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Menu' AND parent_id IS NULL);

INSERT INTO menu_items (parent_id, label, path, icon, permission_name, display_order)
SELECT p.id, 'Homepage Sections', '/admin/sections', 'Layout', 'homepage.view', 1
FROM menu_items p WHERE p.label = 'Content' AND p.parent_id IS NULL
  AND NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Homepage Sections' AND parent_id IS NOT NULL);

INSERT INTO menu_items (parent_id, label, path, icon, permission_name, display_order)
SELECT p.id, 'Popups', '/admin/popups', 'Square', 'homepage.view', 2
FROM menu_items p WHERE p.label = 'Content' AND p.parent_id IS NULL
  AND NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Popups' AND parent_id IS NOT NULL);

-- Homepage sections (seed data for CMS-managed homepage)
-- Only inserts when table is empty to prevent duplicates across restarts
INSERT INTO homepage_sections (section_name, section_type, title, subtitle, description, button_text, button_url, display_order, active, created_by, created_at, updated_at)
SELECT v.* FROM (VALUES
  ('Hero Banner', 'hero_banner', 'Complete Visibility Across Your Channel Ecosystem', 'Enterprise Channel Management Platform', 'Track the complete lifecycle of products across your distribution network — from channel entry to end-customer engagement and activation.', 'Access Platform', '/login', 1, TRUE, 'system', NOW(), NOW()),
  ('Stats Bar', 'stats_bar', 'Stats', NULL, '[{"value":"End-to-End","label":"Product Lifecycle Visibility"},{"value":"Multi-Tier","label":"Channel Ecosystem Support"},{"value":"Role-Based","label":"Access Control"},{"value":"CMS-Driven","label":"Dynamic Content Management"}]', NULL, NULL, 2, TRUE, 'system', NOW(), NOW()),
  ('Product Journey', 'product_journey', 'The Product Journey', 'Follow every product from manufacturer through distribution to end-customer activation.', '[{"title":"Manufacturer","description":"Products enter the channel network from manufacturers and suppliers."},{"title":"Distributor","description":"Distributors receive and forward products to channel partners."},{"title":"Channel Partner","description":"Partners sell products to end customers and manage local inventory."},{"title":"End Customer","description":"Customers purchase products through the partner network."},{"title":"Activation","description":"Products are activated and linked back to their channel journey."}]', NULL, NULL, 3, TRUE, 'system', NOW(), NOW()),
  ('Core Business Areas', 'business_areas', 'Core Business Areas', 'Channel360 is purpose-built to support every stage of the channel ecosystem.', '[{"title":"Channel Entry","description":"Track product movement from manufacturers to distributors and strategic partners. Gain full visibility into initial channel distribution."},{"title":"Partner Transfer","description":"Monitor product flow between distributors and channel partners. Ensure accurate tracking across the entire partner network."},{"title":"Customer Purchase","description":"Track product sales from channel partners to end customers. Capture point-of-sale data for complete revenue visibility."},{"title":"Product Activation","description":"Connect activation records with channel movement data. Enable complete lifecycle tracking for every product unit."},{"title":"Claims Management","description":"Manage and track channel-related claims and incentive programs. Streamline rebates, promotions, and partner compensation."},{"title":"External Data Integration","description":"Upload and manage business data from external sources. Centralize third-party data for unified reporting and analysis."}]', NULL, NULL, 4, TRUE, 'system', NOW(), NOW()),
  ('Benefits', 'benefits', 'Why Channel360', NULL, '[{"title":"Complete Lifecycle Visibility","description":"Track every product from manufacturer to end customer with full activation visibility."},{"title":"Centralized Operations","description":"Manage users, content, announcements, and partner communications in one place."},{"title":"Dynamic Content Control","description":"Update homepage, promotions, and announcements without code deployments."},{"title":"Secure by Design","description":"Role-based access control ensures users see only what they need."},{"title":"Scalable Foundation","description":"Built for enterprise growth with a modular architecture ready for analytics and reporting."}]', NULL, NULL, 5, TRUE, 'system', NOW(), NOW()),
  ('Call to Action', 'cta', 'Ready to Unify Your Channel Operations?', NULL, 'Access the platform to manage your channel ecosystem, track product lifecycles, and gain complete operational visibility.', 'Access Platform', '/login', 6, TRUE, 'system', NOW(), NOW())
) AS v(section_name, section_type, title, subtitle, description, button_text, button_url, display_order, active, created_by, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM homepage_sections);


