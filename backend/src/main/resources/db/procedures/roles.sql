CREATE OR REPLACE PROCEDURE sp_roles_create(
    IN p_name VARCHAR,
    IN p_description VARCHAR,
    INOUT p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO roles (name, description) VALUES (p_name, p_description)
    RETURNING id INTO p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_roles_update(
    IN p_id BIGINT,
    IN p_name VARCHAR,
    IN p_description VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE roles
    SET name = COALESCE(p_name, name),
        description = COALESCE(p_description, description)
    WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_roles_get_by_id(
    IN p_id BIGINT,
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM roles WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_roles_get_all(
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM roles ORDER BY id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_roles_find_by_name(
    IN p_name VARCHAR,
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM roles WHERE name = p_name;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_roles_delete(
    IN p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM user_roles WHERE role_id = p_id;
    DELETE FROM roles WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_roles_exists_by_name(
    IN p_name VARCHAR,
    OUT p_exists BOOLEAN
)
LANGUAGE plpgsql AS $$
BEGIN
    SELECT EXISTS(SELECT 1 FROM roles WHERE name = p_name) INTO p_exists;
END;
$$;
