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
    auth_user_id bigint not null references auth_users(id) on delete cascade,
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
    name varchar(100) not null,
    description text,
    target_entity varchar(100) not null,
    region_id bigint references regions(id) on delete set null,
    is_active boolean not null default true,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS approval_workflow_steps (
    id bigserial PRIMARY KEY,
    workflow_id bigint not null references approval_workflows(id) on delete cascade,
    step_order integer not null,
    approver_role_id bigint references roles(id) on delete set null,
    is_parallel boolean not null default false,
    is_active boolean not null default true,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS approval_requests (
    id bigserial PRIMARY KEY,
    workflow_id bigint not null references approval_workflows(id) on delete cascade,
    requester_id bigint not null references users(id) on delete cascade,
    target_entity_id bigint,
    target_entity_type varchar(100),
    status varchar(20) not null default 'pending',
    priority varchar(20) not null default 'normal',
    submitted_at timestamp not null default current_timestamp,
    completed_at timestamp,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS approval_tasks (
    id bigserial PRIMARY KEY,
    request_id bigint not null references approval_requests(id) on delete cascade,
    step_id bigint not null references approval_workflow_steps(id) on delete cascade,
    assignee_id bigint references users(id) on delete set null,
    status varchar(20) not null default 'pending',
    comments text,
    decided_at timestamp,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id bigserial PRIMARY KEY,
    user_id bigint references users(id) on delete set null,
    username varchar(255),
    action varchar(100) not null,
    entity_type varchar(100),
    entity_id bigint,
    details text,
    ip_address varchar(45),
    created_at timestamp not null default current_timestamp
);

CREATE INDEX IF NOT EXISTS idx_auth_users_email ON auth_users(email);
CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON role_permissions(permission_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_menu_items_parent_id ON menu_items(parent_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_auth_user_id ON refresh_tokens(auth_user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_homepage_sections_section_type ON homepage_sections(section_type);
CREATE INDEX IF NOT EXISTS idx_homepage_sections_active ON homepage_sections(active);
CREATE INDEX IF NOT EXISTS idx_homepage_popups_active ON homepage_popups(active);
CREATE INDEX IF NOT EXISTS idx_regions_parent_id ON regions(parent_id);
CREATE INDEX IF NOT EXISTS idx_region_approvers_region_id ON region_approvers(region_id);
CREATE INDEX IF NOT EXISTS idx_region_approvers_user_id ON region_approvers(user_id);
CREATE INDEX IF NOT EXISTS idx_approval_workflows_target_entity ON approval_workflows(target_entity);
CREATE INDEX IF NOT EXISTS idx_approval_workflow_steps_workflow_id ON approval_workflow_steps(workflow_id);
CREATE INDEX IF NOT EXISTS idx_approval_requests_workflow_id ON approval_requests(workflow_id);
CREATE INDEX IF NOT EXISTS idx_approval_requests_requester_id ON approval_requests(requester_id);
CREATE INDEX IF NOT EXISTS idx_approval_requests_status ON approval_requests(status);
CREATE INDEX IF NOT EXISTS idx_approval_tasks_request_id ON approval_tasks(request_id);
CREATE INDEX IF NOT EXISTS idx_approval_tasks_assignee_id ON approval_tasks(assignee_id);
CREATE INDEX IF NOT EXISTS idx_approval_tasks_status ON approval_tasks(status);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at);

CREATE TABLE IF NOT EXISTS schema_migrations (
    version VARCHAR(255) PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    checksum VARCHAR(64)
);
