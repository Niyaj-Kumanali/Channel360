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
  AND p.name IN ('dashboard.view', 'users.manage')
ON CONFLICT DO NOTHING;

-- ROLE_USER gets dashboard.view only
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_USER'
  AND p.name = 'dashboard.view'
ON CONFLICT DO NOTHING;

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


