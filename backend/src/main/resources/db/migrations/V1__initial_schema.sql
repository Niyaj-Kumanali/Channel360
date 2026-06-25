CREATE TABLE IF NOT EXISTS users (
    id bigserial PRIMARY KEY,
    email varchar(255) not null unique,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    mobile_number varchar(20),
    gender varchar(10),
    address text,
    date_of_birth date,
    profile_image_url text,
    is_active boolean not null default true,
    created_by varchar(255),
    updated_by varchar(255),
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS auth_users (
    id bigserial PRIMARY KEY,
    user_id bigint not null references users(id) on delete cascade,
    email varchar(255) not null unique,
    password varchar(255) not null,
    is_verified boolean not null default false,
    failed_attempts integer not null default 0,
    locked_until timestamp,
    last_login timestamp,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS roles (
    id bigserial PRIMARY KEY,
    name varchar(50) not null unique,
    description varchar(255)
);

CREATE TABLE IF NOT EXISTS permissions (
    id bigserial PRIMARY KEY,
    name varchar(100) not null unique,
    description varchar(255),
    module varchar(50),
    menu_id bigint
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id bigint not null references roles(id) on delete cascade,
    permission_id bigint not null references permissions(id) on delete cascade,
    primary key (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id bigint not null references users(id) on delete cascade,
    role_id bigint not null references roles(id) on delete cascade,
    primary key (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS menu_items (
    id bigserial PRIMARY KEY,
    parent_id bigint references menu_items(id) on delete cascade,
    label varchar(100) not null,
    path varchar(255) not null,
    icon varchar(50),
    permission_name varchar(100),
    display_order integer not null default 0,
    active boolean not null default true,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id bigserial PRIMARY KEY,
    user_id bigint not null references auth_users(id) on delete cascade,
    token varchar(500) not null unique,
    expires_at timestamp not null,
    created_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS homepage_sections (
    id bigserial PRIMARY KEY,
    section_name varchar(100) not null,
    section_type varchar(50) not null,
    title varchar(255) not null,
    subtitle varchar(500),
    description text,
    image_url text,
    button_text varchar(100),
    button_url varchar(500),
    display_order integer not null default 0,
    active boolean not null default true,
    start_date timestamp,
    end_date timestamp,
    created_by varchar(255) not null default 'system',
    updated_by varchar(255),
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS homepage_popups (
    id bigserial PRIMARY KEY,
    title varchar(255) not null,
    description text,
    image_url text,
    cta_button_text varchar(100),
    cta_url varchar(500),
    priority integer not null default 0,
    active boolean not null default true,
    start_date timestamp,
    end_date timestamp,
    created_by varchar(255) not null default 'system',
    updated_by varchar(255),
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS regions (
    id bigserial PRIMARY KEY,
    name varchar(100) not null,
    type varchar(50) not null,
    code varchar(50),
    parent_id bigint references regions(id) on delete cascade,
    is_active boolean not null default true,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS region_approvers (
    id bigserial PRIMARY KEY,
    region_id bigint not null references regions(id) on delete cascade,
    user_id bigint not null references users(id) on delete cascade,
    level integer not null default 1,
    is_active boolean not null default true,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS approval_workflows (
    id bigserial PRIMARY KEY,
    name varchar(255) not null,
    description text,
    module varchar(255),
    active boolean not null default true,
    created_by varchar(255),
    created_at timestamp not null default current_timestamp,
    updated_by varchar(255),
    updated_at timestamp not null default current_timestamp,
    deleted_flag boolean not null default false
);

CREATE TABLE IF NOT EXISTS approval_workflow_steps (
    id bigserial PRIMARY KEY,
    workflow_id bigint not null references approval_workflows(id) on delete cascade,
    step_order integer not null,
    role_name varchar(50) not null,
    label varchar(255) not null,
    mandatory boolean not null default true,
    sla_hours integer,
    escalation_role varchar(50),
    description text,
    created_by varchar(255),
    created_at timestamp not null default current_timestamp,
    updated_by varchar(255),
    updated_at timestamp not null default current_timestamp,
    deleted_flag boolean not null default false
);

CREATE TABLE IF NOT EXISTS approval_requests (
    id bigserial PRIMARY KEY,
    workflow_id bigint not null references approval_workflows(id) on delete cascade,
    request_type varchar(100) not null,
    request_reference_id bigint,
    request_region_id bigint,
    requestor_id bigint not null references users(id) on delete cascade,
    status varchar(50) not null default 'PENDING',
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS approval_tasks (
    id bigserial PRIMARY KEY,
    approval_request_id bigint not null references approval_requests(id) on delete cascade,
    workflow_step_id bigint not null references approval_workflow_steps(id) on delete cascade,
    assigned_role_id bigint not null,
    assigned_user_id bigint,
    assigned_region_id bigint,
    status varchar(50) not null default 'PENDING',
    approved_by bigint,
    approved_at timestamp,
    rejected_by bigint,
    rejected_at timestamp,
    comments text,
    created_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id bigserial PRIMARY KEY,
    user_id bigint,
    action varchar(50) not null,
    module_name varchar(50) not null,
    entity_name varchar(50) not null,
    entity_id bigint,
    old_data jsonb,
    new_data jsonb,
    created_at timestamp not null default current_timestamp
);

CREATE INDEX IF NOT EXISTS idx_auth_users_email ON auth_users(email);
CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON role_permissions(permission_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_menu_items_parent_id ON menu_items(parent_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_homepage_sections_section_type ON homepage_sections(section_type);
CREATE INDEX IF NOT EXISTS idx_homepage_sections_active ON homepage_sections(active);
CREATE INDEX IF NOT EXISTS idx_homepage_popups_active ON homepage_popups(active);
CREATE INDEX IF NOT EXISTS idx_regions_parent_id ON regions(parent_id);
CREATE INDEX IF NOT EXISTS idx_region_approvers_region_id ON region_approvers(region_id);
CREATE INDEX IF NOT EXISTS idx_region_approvers_user_id ON region_approvers(user_id);
CREATE INDEX IF NOT EXISTS idx_approval_workflow_steps_workflow_id ON approval_workflow_steps(workflow_id);
CREATE INDEX IF NOT EXISTS idx_approval_requests_workflow_id ON approval_requests(workflow_id);
CREATE INDEX IF NOT EXISTS idx_approval_requests_requestor_id ON approval_requests(requestor_id);
CREATE INDEX IF NOT EXISTS idx_approval_requests_status ON approval_requests(status);
CREATE INDEX IF NOT EXISTS idx_approval_tasks_approval_request_id ON approval_tasks(approval_request_id);
CREATE INDEX IF NOT EXISTS idx_approval_tasks_assigned_user_id ON approval_tasks(assigned_user_id);
CREATE INDEX IF NOT EXISTS idx_approval_tasks_assigned_role_id ON approval_tasks(assigned_role_id);
CREATE INDEX IF NOT EXISTS idx_approval_tasks_workflow_step_id ON approval_tasks(workflow_step_id);
CREATE INDEX IF NOT EXISTS idx_approval_tasks_status ON approval_tasks(status);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_module_name ON audit_logs(module_name);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity_name ON audit_logs(entity_name);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at);

CREATE TABLE IF NOT EXISTS schema_migrations (
    version VARCHAR(255) PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    checksum VARCHAR(64)
);
