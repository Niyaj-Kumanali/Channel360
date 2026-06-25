-- =============================================
-- Full database reset
-- Drops all procedures, tables, indexes, sequences
-- =============================================

-- 0. Drop all stored procedures first
DROP PROCEDURE IF EXISTS sp_auth_change_password;
DROP PROCEDURE IF EXISTS sp_auth_update_last_login;
DROP PROCEDURE IF EXISTS sp_auth_users_save;
DROP PROCEDURE IF EXISTS sp_homepage_popup_delete;
DROP PROCEDURE IF EXISTS sp_homepage_popup_get;
DROP PROCEDURE IF EXISTS sp_homepage_popup_save;
DROP PROCEDURE IF EXISTS sp_homepage_popups_active;
DROP PROCEDURE IF EXISTS sp_homepage_popups_list;
DROP PROCEDURE IF EXISTS sp_homepage_section_delete;
DROP PROCEDURE IF EXISTS sp_homepage_section_get;
DROP PROCEDURE IF EXISTS sp_homepage_section_save;
DROP PROCEDURE IF EXISTS sp_homepage_sections_list;
DROP PROCEDURE IF EXISTS sp_homepage_sections_published;
DROP PROCEDURE IF EXISTS sp_refresh_tokens_delete_by_user_id;
DROP PROCEDURE IF EXISTS sp_refresh_tokens_revoke;
DROP PROCEDURE IF EXISTS sp_refresh_tokens_save;
DROP PROCEDURE IF EXISTS sp_region_approver_deactivate;
DROP PROCEDURE IF EXISTS sp_region_approver_save;
DROP PROCEDURE IF EXISTS sp_regions_delete;
DROP PROCEDURE IF EXISTS sp_regions_save;
DROP PROCEDURE IF EXISTS sp_roles_delete;
DROP PROCEDURE IF EXISTS sp_roles_list;
DROP PROCEDURE IF EXISTS sp_roles_save;
DROP PROCEDURE IF EXISTS sp_users_assign_roles;
DROP PROCEDURE IF EXISTS sp_users_count;
DROP PROCEDURE IF EXISTS sp_users_delete;
DROP PROCEDURE IF EXISTS sp_users_list;
DROP PROCEDURE IF EXISTS sp_users_save;
DROP PROCEDURE IF EXISTS sp_workflow_delete;
DROP PROCEDURE IF EXISTS sp_workflow_save;
DROP PROCEDURE IF EXISTS sp_workflow_step_delete;
DROP PROCEDURE IF EXISTS sp_workflow_step_save;

-- 1. Drop FK constraints that create circular deps
ALTER TABLE IF EXISTS permissions DROP CONSTRAINT IF EXISTS permissions_menu_id_fkey;
ALTER TABLE IF EXISTS menu_items DROP CONSTRAINT IF EXISTS menu_items_permission_name_fkey;
ALTER TABLE IF EXISTS menu_items DROP CONSTRAINT IF EXISTS menu_items_parent_id_fkey;

-- 2. Drop tables in safe order
DROP TABLE IF EXISTS approval_tasks CASCADE;
DROP TABLE IF EXISTS approval_requests CASCADE;
DROP TABLE IF EXISTS region_approvers CASCADE;
DROP TABLE IF EXISTS approval_workflow_steps CASCADE;
DROP TABLE IF EXISTS approval_workflows CASCADE;
DROP TABLE IF EXISTS regions CASCADE;
DROP TABLE IF EXISTS menu_item_roles CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS menu_items CASCADE;
DROP TABLE IF EXISTS homepage_popups CASCADE;
DROP TABLE IF EXISTS homepage_sections CASCADE;
DROP TABLE IF EXISTS password_reset_tokens CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS auth_users CASCADE;
DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- 3. Drop indexes
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
DROP INDEX IF EXISTS idx_audit_logs_user_id;
DROP INDEX IF EXISTS idx_audit_logs_entity_type;
DROP INDEX IF EXISTS idx_regions_parent_id;
DROP INDEX IF EXISTS idx_regions_code;
DROP INDEX IF EXISTS idx_approval_workflows_entity;
DROP INDEX IF EXISTS idx_workflow_steps_workflow_id;
DROP INDEX IF EXISTS idx_approval_requests_entity;
DROP INDEX IF EXISTS idx_approval_tasks_approval_id;
DROP INDEX IF EXISTS idx_user_roles_user_id;
DROP INDEX IF EXISTS idx_user_roles_role_id;
