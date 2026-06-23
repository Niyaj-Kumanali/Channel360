CREATE OR REPLACE PROCEDURE sp_region_approver_save(
    INOUT p_id BIGINT,
    IN p_region_id BIGINT,
    IN p_role_id BIGINT,
    IN p_user_id BIGINT,
    IN p_active_flag BOOLEAN,
    IN p_user VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_id IS NULL THEN
        INSERT INTO region_approvers (region_id, role_id, user_id, active_flag, created_by, created_at, updated_at)
        VALUES (p_region_id, p_role_id, p_user_id, COALESCE(p_active_flag, TRUE), p_user, NOW(), NOW())
        RETURNING id INTO p_id;
    ELSE
        UPDATE region_approvers
        SET region_id = COALESCE(p_region_id, region_id),
            role_id = COALESCE(p_role_id, role_id),
            user_id = COALESCE(p_user_id, user_id),
            active_flag = COALESCE(p_active_flag, active_flag),
            updated_by = p_user,
            updated_at = NOW()
        WHERE id = p_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_region_approver_deactivate(
    IN p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE region_approvers SET active_flag = FALSE, updated_at = NOW() WHERE id = p_id;
END;
$$;
