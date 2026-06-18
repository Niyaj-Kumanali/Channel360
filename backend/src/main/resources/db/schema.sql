CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(255),
    employee_id VARCHAR(255) UNIQUE,
    status VARCHAR(255) DEFAULT 'ACTIVE',
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    deleted_flag BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS homepage_sections (
    id BIGSERIAL PRIMARY KEY,
    section_name VARCHAR(100) NOT NULL,
    section_type VARCHAR(50) NOT NULL,
    title VARCHAR(200),
    subtitle VARCHAR(300),
    description TEXT,
    image_url VARCHAR(500),
    button_text VARCHAR(100),
    button_url VARCHAR(500),
    display_order INTEGER,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    deleted_flag BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS homepage_popups (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    cta_button_text VARCHAR(100),
    cta_url VARCHAR(500),
    priority INTEGER,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    deleted_flag BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_homepage_sections_active_dates ON homepage_sections(active, start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_homepage_sections_display_order ON homepage_sections(display_order);
CREATE INDEX IF NOT EXISTS idx_homepage_popups_active_dates ON homepage_popups(active, start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_homepage_popups_priority ON homepage_popups(priority);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_employee_id ON users(employee_id);
