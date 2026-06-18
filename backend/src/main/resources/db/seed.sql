INSERT INTO roles (name, description)
VALUES ('ROLE_ADMIN', 'Administrator with full access')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description)
VALUES ('ROLE_USER', 'Regular user with basic access')
ON CONFLICT (name) DO NOTHING;
