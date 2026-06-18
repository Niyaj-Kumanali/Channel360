CREATE OR REPLACE PROCEDURE sp_users_create(
    IN p_first_name VARCHAR,
    IN p_last_name VARCHAR,
    IN p_email VARCHAR,
    IN p_password VARCHAR,
    IN p_mobile_number VARCHAR,
    IN p_employee_id VARCHAR,
    IN p_status VARCHAR,
    IN p_created_by VARCHAR,
    INOUT p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO users (first_name, last_name, email, password, mobile_number, employee_id, status, created_by, created_at, updated_at)
    VALUES (p_first_name, p_last_name, p_email, p_password, p_mobile_number, p_employee_id, COALESCE(p_status, 'ACTIVE'), p_created_by, NOW(), NOW())
    RETURNING id INTO p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_update(
    IN p_id BIGINT,
    IN p_first_name VARCHAR,
    IN p_last_name VARCHAR,
    IN p_email VARCHAR,
    IN p_mobile_number VARCHAR,
    IN p_employee_id VARCHAR,
    IN p_status VARCHAR,
    IN p_modified_by VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE users
    SET first_name = COALESCE(p_first_name, first_name),
        last_name = COALESCE(p_last_name, last_name),
        email = COALESCE(p_email, email),
        mobile_number = COALESCE(p_mobile_number, mobile_number),
        employee_id = COALESCE(p_employee_id, employee_id),
        status = COALESCE(p_status, status),
        last_modified_by = p_modified_by,
        updated_at = NOW()
    WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_get_by_id(
    IN p_id BIGINT,
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM users WHERE id = p_id AND deleted_flag = FALSE;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_get_by_email(
    IN p_email VARCHAR,
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM users WHERE email = p_email AND deleted_flag = FALSE;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_exists_by_email(
    IN p_email VARCHAR,
    OUT p_exists BOOLEAN
)
LANGUAGE plpgsql AS $$
BEGIN
    SELECT EXISTS(SELECT 1 FROM users WHERE email = p_email AND deleted_flag = FALSE) INTO p_exists;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_exists_by_employee_id(
    IN p_employee_id VARCHAR,
    OUT p_exists BOOLEAN
)
LANGUAGE plpgsql AS $$
BEGIN
    SELECT EXISTS(SELECT 1 FROM users WHERE employee_id = p_employee_id AND deleted_flag = FALSE) INTO p_exists;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_delete(
    IN p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM user_roles WHERE user_id = p_id;
    DELETE FROM refresh_tokens WHERE user_id = p_id;
    DELETE FROM users WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_activate(
    IN p_id BIGINT,
    IN p_modified_by VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE users SET status = 'ACTIVE', last_modified_by = p_modified_by, updated_at = NOW()
    WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_deactivate(
    IN p_id BIGINT,
    IN p_modified_by VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE users SET status = 'INACTIVE', last_modified_by = p_modified_by, updated_at = NOW()
    WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_assign_roles(
    IN p_user_id BIGINT,
    IN p_role_ids TEXT,
    IN p_modified_by VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM user_roles WHERE user_id = p_user_id;
    INSERT INTO user_roles (user_id, role_id)
    SELECT p_user_id, regexp_split_to_table(p_role_ids, ',')::BIGINT;
    UPDATE users SET last_modified_by = p_modified_by, updated_at = NOW() WHERE id = p_user_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_change_password(
    IN p_id BIGINT,
    IN p_password VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE users SET password = p_password, updated_at = NOW() WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_update_last_login(
    IN p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE users SET last_login_at = NOW() WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_exists_by_id(
    IN p_id BIGINT,
    OUT p_exists BOOLEAN
)
LANGUAGE plpgsql AS $$
BEGIN
    SELECT EXISTS(SELECT 1 FROM users WHERE id = p_id AND deleted_flag = FALSE) INTO p_exists;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_get_roles(
    IN p_user_id BIGINT,
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR
        SELECT r.* FROM roles r
        JOIN user_roles ur ON ur.role_id = r.id
        WHERE ur.user_id = p_user_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_users_list_paginated(
    IN p_search TEXT,
    IN p_status VARCHAR,
    IN p_role_id BIGINT,
    IN p_page INTEGER,
    IN p_size INTEGER,
    IN p_sort_by TEXT,
    IN p_sort_dir TEXT,
    INOUT p_data REFCURSOR,
    OUT p_total_count BIGINT
)
LANGUAGE plpgsql AS $$
DECLARE
    v_offset INTEGER;
    v_query TEXT;
    v_count_query TEXT;
    v_conditions TEXT[] := '{}';
BEGIN
    v_offset := p_page * p_size;

    IF p_search IS NOT NULL AND p_search <> '' THEN
        v_conditions := array_append(v_conditions,
            format('(u.first_name ILIKE %s OR u.last_name ILIKE %s OR u.email ILIKE %s OR u.employee_id ILIKE %s)',
                   quote_literal('%' || p_search || '%'),
                   quote_literal('%' || p_search || '%'),
                   quote_literal('%' || p_search || '%'),
                   quote_literal('%' || p_search || '%')));
    END IF;

    IF p_status IS NOT NULL AND p_status <> '' THEN
        v_conditions := array_append(v_conditions, format('u.status = %s', quote_literal(p_status)));
    END IF;

    IF p_role_id IS NOT NULL THEN
        v_conditions := array_append(v_conditions, format('EXISTS(SELECT 1 FROM user_roles ur2 WHERE ur2.user_id = u.id AND ur2.role_id = %s)', p_role_id));
    END IF;

    v_query := 'SELECT * FROM users u WHERE u.deleted_flag = FALSE';
    IF array_length(v_conditions, 1) > 0 THEN
        v_query := v_query || ' AND ' || array_to_string(v_conditions, ' AND ');
    END IF;

    v_count_query := 'SELECT COUNT(*) FROM users u WHERE u.deleted_flag = FALSE';
    IF array_length(v_conditions, 1) > 0 THEN
        v_count_query := v_count_query || ' AND ' || array_to_string(v_conditions, ' AND ');
    END IF;

    v_query := v_query || format(' ORDER BY %I %s', COALESCE(p_sort_by, 'created_at'), CASE WHEN lower(p_sort_dir) = 'asc' THEN 'ASC' ELSE 'DESC' END);
    v_query := v_query || format(' LIMIT %s OFFSET %s', p_size, v_offset);

    OPEN p_data FOR EXECUTE v_query;
    EXECUTE v_count_query INTO p_total_count;
END;
$$;
