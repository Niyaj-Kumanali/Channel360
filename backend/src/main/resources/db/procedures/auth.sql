CREATE OR REPLACE PROCEDURE sp_refresh_tokens_create(
    IN p_user_id BIGINT,
    IN p_token VARCHAR,
    IN p_expiry_date TIMESTAMP,
    INOUT p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO refresh_tokens (token, user_id, expiry_date, revoked, created_at)
    VALUES (p_token, p_user_id, p_expiry_date, FALSE, NOW())
    RETURNING id INTO p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_refresh_tokens_find_by_token(
    IN p_token VARCHAR,
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM refresh_tokens WHERE token = p_token;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_refresh_tokens_find_by_user_id(
    IN p_user_id BIGINT,
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM refresh_tokens WHERE user_id = p_user_id ORDER BY created_at DESC;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_refresh_tokens_revoke(
    IN p_token VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE refresh_tokens SET revoked = TRUE WHERE token = p_token;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_refresh_tokens_delete_by_user_id(
    IN p_user_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM refresh_tokens WHERE user_id = p_user_id;
END;
$$;
