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
VALUES ('sections.view', 'View homepage sections', 'sections')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('sections.create', 'Create homepage sections', 'sections')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('sections.edit', 'Edit homepage sections', 'sections')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('sections.delete', 'Delete homepage sections', 'sections')
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (name, description, module)
VALUES ('popups.view', 'View homepage popups', 'popups')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('popups.create', 'Create homepage popups', 'popups')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('popups.edit', 'Edit homepage popups', 'popups')
ON CONFLICT (name) DO NOTHING;
INSERT INTO permissions (name, description, module)
VALUES ('popups.delete', 'Delete homepage popups', 'popups')
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (name, description, module)
VALUES ('menu.manage', 'Manage sidebar menu items', 'menu')
ON CONFLICT (name) DO NOTHING;

-- ROLE_SUPER_ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_SUPER_ADMIN'
ON CONFLICT DO NOTHING;

-- ROLE_ADMIN gets dashboard.view + users.* only (not content/permissions/menu)
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

-- Menu items (role-visible, idempotent per-item inserts)
INSERT INTO menu_items (label, path, icon, display_order, permission_name)
SELECT 'Dashboard', '/dashboard', 'LayoutDashboard', 1, 'dashboard.view'
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Dashboard' AND parent_id IS NULL);

INSERT INTO menu_items (label, path, icon, display_order, permission_name)
SELECT 'Content', '#', 'FileText', 2, NULL
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Content' AND parent_id IS NULL);

INSERT INTO menu_items (label, path, icon, display_order, permission_name)
SELECT 'Roles', '/admin/roles', 'Shield', 3, 'roles.view'
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Roles' AND parent_id IS NULL);

INSERT INTO menu_items (label, path, icon, display_order, permission_name)
SELECT 'Menu', '/admin/menu', 'Menu', 4, 'menu.manage'
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Menu' AND parent_id IS NULL);

INSERT INTO menu_items (parent_id, label, path, icon, display_order, permission_name)
SELECT p.id, 'Homepage Sections', '/admin/sections', 'Layout', 1, 'sections.view'
FROM menu_items p WHERE p.label = 'Content' AND p.parent_id IS NULL
  AND NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Homepage Sections' AND parent_id IS NOT NULL);

INSERT INTO menu_items (parent_id, label, path, icon, display_order, permission_name)
SELECT p.id, 'Popups', '/admin/popups', 'Square', 2, 'popups.view'
FROM menu_items p WHERE p.label = 'Content' AND p.parent_id IS NULL
  AND NOT EXISTS (SELECT 1 FROM menu_items WHERE label = 'Popups' AND parent_id IS NOT NULL);

-- Map each permission to its controlling menu item
UPDATE permissions SET menu_id = (SELECT id FROM menu_items WHERE label = 'Dashboard' AND parent_id IS NULL)           WHERE name = 'dashboard.view';
UPDATE permissions SET menu_id = (SELECT id FROM menu_items WHERE label = 'Roles' AND parent_id IS NULL)                WHERE module = 'roles';
UPDATE permissions SET menu_id = (SELECT id FROM menu_items WHERE label = 'Menu' AND parent_id IS NULL)                 WHERE module = 'menu';
UPDATE permissions SET menu_id = (SELECT id FROM menu_items WHERE label = 'Homepage Sections')                          WHERE module = 'sections';
UPDATE permissions SET menu_id = (SELECT id FROM menu_items WHERE label = 'Popups')                                     WHERE module = 'popups';

-- Homepage sections (seed data for CMS-managed homepage)
-- Only inserts when table is empty to prevent duplicates across restarts
INSERT INTO homepage_sections (section_name, section_type, title, subtitle, description, button_text, button_url, display_order, active, created_by, created_at, updated_at)
SELECT v.* FROM (VALUES
  ('Hero Banner', 'hero_banner', 'Complete Visibility Across Your Channel Ecosystem', NULL, 'Track the complete lifecycle of products across your distribution network, from channel entry to end-customer engagement and activation.', 'Access Platform', '/login', 1, TRUE, 'system', NOW(), NOW()),
  ('Product Journey', 'product_journey', 'The Product Journey', 'Follow every product from manufacturer through distribution to end-customer activation.', '[{"title":"Manufacturer","description":"Products enter the channel network from manufacturers and suppliers."},{"title":"Distributor","description":"Distributors receive and forward products to channel partners."},{"title":"Channel Partner","description":"Partners sell products to end customers and manage local inventory."},{"title":"End Customer","description":"Customers purchase products through the partner network."},{"title":"Activation","description":"Products are activated and linked back to their channel journey."}]', NULL, NULL, 2, TRUE, 'system', NOW(), NOW()),
  ('Platform Capabilities', 'platform_capabilities', 'Platform Capabilities', 'Powerful modules purpose-built for end-to-end channel ecosystem management.', '[{"title":"Channel Analytics","description":"Real-time dashboards, reports, and actionable insights across the entire channel network."},{"title":"Partner Lifecycle","description":"Streamlined onboarding, performance tracking, and relationship management for every partner."},{"title":"Claims & Incentives","description":"Automated rebate, claim, and incentive program management with real-time tracking."},{"title":"Compliance Management","description":"Automated compliance checks, audit trails, and regulatory reporting across markets."},{"title":"Data Integration Hub","description":"Centralized data ingestion from ERP, CRM, and external partner systems."},{"title":"Smart Notifications","description":"Configurable alerts for inventory thresholds, claim status, and partner activity."}]', NULL, NULL, 3, TRUE, 'system', NOW(), NOW()),
  ('Benefits', 'benefits', 'Why Channel360?', NULL, '[{"title":"Complete Lifecycle Visibility","description":"Track every product from manufacturer to end customer with full activation visibility."},{"title":"Centralized Operations","description":"Manage users, content, announcements, and partner communications in one place."},{"title":"Dynamic Content Control","description":"Update homepage, promotions, and announcements without code deployments."},{"title":"Secure by Design","description":"Role-based access control ensures users see only what they need."},{"title":"Scalable Foundation","description":"Built for enterprise growth with a modular architecture ready for analytics and reporting."}]', NULL, NULL, 4, TRUE, 'system', NOW(), NOW()),
  ('Contact', 'contact', 'Partner with Channel360', 'Let''s build the next big thing together. Tell us about your channel goals and we''ll map out a path forward.', '[{"type":"email","label":"Email","value":"niyajkumanali@gmail.com"},{"type":"phone","label":"Phone","value":"+91 8217097121"},{"type":"address","label":"Office","value":"Nipani, Belagavi, Karnataka, India, 591237"},{"type":"linkedin","label":"LinkedIn","value":"linkedin.com/in/niyaj-kumanali"},{"type":"github","label":"GitHub","value":"github.com/Niyaj-Kumanali"},{"type":"website","label":"Portfolio","value":"niyazdev.vercel.app"}]', 'Access Platform', '/login', 5, TRUE, 'system', NOW(), NOW()),
  ('Footer', 'footer', NULL, NULL, '{"tagline":"End-to-end visibility across your entire channel ecosystem — from manufacturer to end-customer activation.","groups":[{"title":"Platform","links":[{"label":"Home","url":"/"},{"label":"Features","url":"#"},{"label":"Pricing","url":"#"},{"label":"FAQ","url":"#"}]},{"title":"Resources","links":[{"label":"Documentation","url":"#"},{"label":"API Reference","url":"#"},{"label":"Changelog","url":"#"},{"label":"Status","url":"#"}]},{"title":"Company","links":[{"label":"About","url":"#"},{"label":"Blog","url":"#"},{"label":"Careers","url":"#"},{"label":"Contact","url":"#"}]}],"socialLinks":[{"label":"Email","url":"mailto:niyajkumanali@gmail.com","icon":"email"},{"label":"LinkedIn","url":"https://linkedin.com/in/niyaj-kumanali","icon":"linkedin"},{"label":"GitHub","url":"https://github.com/Niyaj-Kumanali","icon":"github"},{"label":"Portfolio","url":"https://niyazdev.vercel.app","icon":"website"}]}', NULL, NULL, 6, TRUE, 'system', NOW(), NOW()),
  ('Announcement', 'announcement', 'New Partner Program Launching Q3', NULL, 'We are excited to announce enhanced incentive tiers and streamlined onboarding for all channel partners.', 'Learn More', '#', 7, FALSE, 'system', NOW(), NOW()),
  ('Information Block', 'info_block', 'How Channel360 Works', 'A unified platform for your entire distribution network', 'Channel360 connects manufacturers, distributors, and channel partners on a single platform, providing end-to-end visibility, automated workflows, and actionable insights.', 'Learn More', '#', 8, FALSE, 'system', NOW(), NOW()),
  ('Promotion', 'promotion', 'Early Access: Partner Insights Dashboard', 'Beta Program', 'Be among the first to explore our new Partner Insights Dashboard with real-time performance metrics, trend analysis, and actionable recommendations.', 'Join Beta', '#', 9, FALSE, 'system', NOW(), NOW()),
  ('Image Card', 'image_card', 'Platform Dashboard Preview', NULL, 'Get a first look at the new analytics dashboard with real-time channel performance metrics.', 'View Details', '#', 10, FALSE, 'system', NOW(), NOW()),
  ('Rich Content', 'rich_content', 'Frequently Asked Questions', NULL, '<h3>How does onboarding work?</h3><p>Our team guides you through every step, from data migration to partner training.</p><h3>Can I integrate with my existing ERP?</h3><p>Yes, Channel360 supports integrations with major ERP and CRM systems.</p><h3>What kind of support is available?</h3><p>24/7 technical support with dedicated account managers for enterprise plans.</p><h3>Is my data secure on the platform?</h3><p>We use enterprise-grade encryption, SOC 2 compliance, and role-based access controls to keep your data safe.</p><h3>Can I customize the platform for my workflow?</h3><p>Yes, Channel360 supports custom fields, workflows, and modules tailored to your distribution model.</p><h3>What does pricing look like for enterprises?</h3><p>We offer tiered pricing based on partner count and transaction volume. Contact our sales team for a custom quote.</p>', NULL, NULL, 11, FALSE, 'system', NOW(), NOW())
) AS v(section_name, section_type, title, subtitle, description, button_text, button_url, display_order, active, created_by, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM homepage_sections);


