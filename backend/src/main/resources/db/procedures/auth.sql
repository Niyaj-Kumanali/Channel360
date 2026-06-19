CREATE OR REPLACE PROCEDURE sp_refresh_tokens_save(
    INOUT p_id BIGINT,
    IN p_token VARCHAR,
    IN p_user_id BIGINT,
    IN p_expiry_date TIMESTAMP,
    IN p_revoked BOOLEAN
)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_id IS NULL THEN
        INSERT INTO refresh_tokens (token, user_id, expiry_date, revoked, created_at)
        VALUES (p_token, p_user_id, p_expiry_date, COALESCE(p_revoked, FALSE), NOW())
        RETURNING id INTO p_id;
    ELSE
        UPDATE refresh_tokens SET revoked = p_revoked WHERE id = p_id;
    END IF;
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
