CREATE OR REPLACE PROCEDURE sp_roles_save(
    INOUT p_id BIGINT,
    IN p_name VARCHAR,
    IN p_description VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_id IS NULL THEN
        INSERT INTO roles (name, description) VALUES (p_name, p_description) RETURNING id INTO p_id;
    ELSE
        UPDATE roles SET name = p_name, description = p_description WHERE id = p_id;
    END IF;
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

CREATE OR REPLACE PROCEDURE sp_roles_list(
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM roles ORDER BY id;
END;
$$;
