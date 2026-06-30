CREATE TABLE IF NOT EXISTS users (
    id bigserial PRIMARY KEY,
    email varchar(255) not null unique,
    password varchar(255) not null,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    mobile_number varchar(20),
    employee_id varchar(100) unique,
    gender varchar(10),
    address text,
    date_of_birth date,
    profile_image_url text,
    status varchar(50) not null default 'ACTIVE',
    deleted_flag boolean not null default false,
    last_login_at timestamp,
    created_by varchar(255),
    last_modified_by varchar(255),
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
    user_id bigint not null references users(id) on delete cascade,
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
