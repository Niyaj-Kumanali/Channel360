-- Channel360 Initial Database Schema
-- PostgreSQL

-- Drop tables if they exist (for development reset)
DROP TABLE IF EXISTS homepage_popups CASCADE;
DROP TABLE IF EXISTS homepage_sections CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- ============================================
-- 1. ROLES
-- ============================================
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- ============================================
-- 2. USERS
-- ============================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    mobile_number VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_at TIMESTAMP,
    created_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_flag BOOLEAN NOT NULL DEFAULT FALSE
);

-- ============================================
-- 3. USER_ROLES (Many-to-Many)
-- ============================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ============================================
-- 4. REFRESH TOKENS
-- ============================================
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- 5. HOMEPAGE SECTIONS (CMS)
-- ============================================
CREATE TABLE homepage_sections (
    id BIGSERIAL PRIMARY KEY,
    section_name VARCHAR(100) NOT NULL,
    section_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    description TEXT,
    image_url VARCHAR(500),
    button_text VARCHAR(100),
    button_url VARCHAR(500),
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_flag BOOLEAN NOT NULL DEFAULT FALSE
);

-- ============================================
-- 6. HOMEPAGE POPUPS
-- ============================================
CREATE TABLE homepage_popups (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    cta_button_text VARCHAR(100),
    cta_url VARCHAR(500),
    priority INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_flag BOOLEAN NOT NULL DEFAULT FALSE
);

-- ============================================
-- INDEXES
-- ============================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_employee_id ON users(employee_id);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_deleted_flag ON users(deleted_flag);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

CREATE INDEX idx_homepage_sections_active ON homepage_sections(active);
CREATE INDEX idx_homepage_sections_display_order ON homepage_sections(display_order);
CREATE INDEX idx_homepage_sections_type ON homepage_sections(section_type);
CREATE INDEX idx_homepage_sections_deleted_flag ON homepage_sections(deleted_flag);

CREATE INDEX idx_homepage_popups_active ON homepage_popups(active);
CREATE INDEX idx_homepage_popups_priority ON homepage_popups(priority);
CREATE INDEX idx_homepage_popups_deleted_flag ON homepage_popups(deleted_flag);

-- ============================================
-- SEED DATA
-- ============================================
INSERT INTO roles (name, description) VALUES
    ('ROLE_SUPER_ADMIN', 'Platform administrator — manages users, roles, menus, CMS, workflows, regions. No channel data access.'),
    ('ROLE_ADMIN', 'Highest business authority — full operational access, all regions, override approval chains.'),
    ('ROLE_MANAGER', 'Geographically scoped business approver — authority determined by assigned region.'),
    ('ROLE_INTERNAL_EMPLOYEE', 'Internal company employee — operations, sales, finance, product team.'),
    ('ROLE_EXTERNAL_EMPLOYEE', 'Contractor or outsourced user with limited operational access.'),
    ('ROLE_DISTRIBUTOR', 'Distributor partner — scoped to own company. Uploads sales, manages inventory.'),
    ('ROLE_CHANNEL_PARTNER', 'Channel partner — scoped to own company. Customer sales, activations.'),
    ('ROLE_GUEST', 'Default registration role — minimal dashboard access.');
