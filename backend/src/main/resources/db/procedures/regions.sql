CREATE OR REPLACE PROCEDURE sp_regions_save(
    INOUT p_id BIGINT,
    IN p_name VARCHAR,
    IN p_parent_id BIGINT,
    IN p_level VARCHAR,
    IN p_tree_type VARCHAR,
    IN p_user VARCHAR
)
LANGUAGE plpgsql AS $$
DECLARE
    v_parent_path TEXT;
BEGIN
    IF p_parent_id IS NOT NULL THEN
        SELECT path INTO v_parent_path FROM regions WHERE id = p_parent_id AND deleted_flag = FALSE;
    END IF;

    IF p_id IS NULL THEN
        INSERT INTO regions (name, parent_id, level, tree_type, path, created_by, created_at, updated_at)
        VALUES (p_name, p_parent_id, p_level, p_tree_type,
                COALESCE(v_parent_path || '/' || p_name, p_name),
                p_user, NOW(), NOW())
        RETURNING id INTO p_id;
    ELSE
        UPDATE regions
        SET name = COALESCE(p_name, name),
            parent_id = COALESCE(p_parent_id, parent_id),
            level = COALESCE(p_level, level),
            tree_type = COALESCE(p_tree_type, tree_type),
            path = CASE WHEN p_name IS NOT NULL THEN
                COALESCE(v_parent_path || '/' || p_name, p_name)
              ELSE path END,
            updated_by = p_user,
            updated_at = NOW()
        WHERE id = p_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_regions_delete(
    IN p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE regions SET deleted_flag = TRUE, updated_at = NOW() WHERE id = p_id;
END;
$$;
