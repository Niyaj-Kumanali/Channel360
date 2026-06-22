CREATE OR REPLACE FUNCTION sp_audit_log_insert(
    p_user_id BIGINT,
    p_action VARCHAR(50),
    p_module_name VARCHAR(50),
    p_entity_name VARCHAR(50),
    p_entity_id BIGINT,
    p_old_data JSONB,
    p_new_data JSONB
) RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    v_id BIGINT;
BEGIN
    INSERT INTO audit_logs (user_id, action, module_name, entity_name, entity_id, old_data, new_data, created_at)
    VALUES (p_user_id, p_action, p_module_name, p_entity_name, p_entity_id, p_old_data, p_new_data, NOW())
    RETURNING id INTO v_id;

    RETURN v_id;
END;
$$;
