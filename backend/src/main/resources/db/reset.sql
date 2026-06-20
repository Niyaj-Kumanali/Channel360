-- =============================================
-- Drop everything (respecting circular FK deps)
-- =============================================

-- 1. Drop FK constraints that create circular deps
ALTER TABLE IF EXISTS permissions DROP CONSTRAINT IF EXISTS permissions_menu_id_fkey;
ALTER TABLE IF EXISTS menu_items DROP CONSTRAINT IF EXISTS menu_items_permission_name_fkey;

-- 2. Drop FK constraint on menu_items.parent_id (self-ref)
ALTER TABLE IF EXISTS menu_items DROP CONSTRAINT IF EXISTS menu_items_parent_id_fkey;

-- 3. Drop tables in safe order
DROP TABLE IF EXISTS menu_item_roles CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS menu_items CASCADE;
DROP TABLE IF EXISTS homepage_popups CASCADE;
DROP TABLE IF EXISTS homepage_sections CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- 4. Drop indexes (just in case)
DROP INDEX IF EXISTS idx_users_email;
DROP INDEX IF EXISTS idx_users_employee_id;
DROP INDEX IF EXISTS idx_homepage_sections_active;
DROP INDEX IF EXISTS idx_homepage_sections_display_order;
DROP INDEX IF EXISTS idx_homepage_sections_type;
DROP INDEX IF EXISTS idx_homepage_sections_deleted_flag;
DROP INDEX IF EXISTS idx_homepage_popups_active;
DROP INDEX IF EXISTS idx_homepage_popups_deleted_flag;
DROP INDEX IF EXISTS idx_menu_items_parent_id;
DROP INDEX IF EXISTS idx_menu_items_permission;
DROP INDEX IF EXISTS idx_menu_items_display_order;
DROP INDEX IF EXISTS idx_permissions_menu_id;
DROP INDEX IF EXISTS idx_refresh_tokens_user_id;
DROP INDEX IF EXISTS idx_refresh_tokens_token;
