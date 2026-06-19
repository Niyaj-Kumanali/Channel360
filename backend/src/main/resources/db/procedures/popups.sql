CREATE OR REPLACE PROCEDURE sp_homepage_popups_save(
    INOUT p_id BIGINT,
    IN p_title VARCHAR,
    IN p_description TEXT,
    IN p_image_url VARCHAR,
    IN p_cta_button_text VARCHAR,
    IN p_cta_url VARCHAR,
    IN p_priority INTEGER,
    IN p_active BOOLEAN,
    IN p_start_date TIMESTAMP,
    IN p_end_date TIMESTAMP,
    IN p_created_by VARCHAR,
    IN p_modified_by VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_id IS NULL THEN
        INSERT INTO homepage_popups (title, description, image_url, cta_button_text, cta_url, priority, active, start_date, end_date, created_by, created_at, updated_at)
        VALUES (p_title, p_description, p_image_url, p_cta_button_text, p_cta_url, p_priority, COALESCE(p_active, FALSE), p_start_date, p_end_date, p_created_by, NOW(), NOW())
        RETURNING id INTO p_id;
    ELSE
        UPDATE homepage_popups
        SET title = COALESCE(p_title, title),
            description = COALESCE(p_description, description),
            image_url = COALESCE(p_image_url, image_url),
            cta_button_text = COALESCE(p_cta_button_text, cta_button_text),
            cta_url = COALESCE(p_cta_url, cta_url),
            priority = COALESCE(p_priority, priority),
            active = COALESCE(p_active, active),
            start_date = COALESCE(p_start_date, start_date),
            end_date = COALESCE(p_end_date, end_date),
            last_modified_by = p_modified_by,
            updated_at = NOW()
        WHERE id = p_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_popups_delete(
    IN p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM homepage_popups WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_popups_get_active(
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR
        SELECT * FROM homepage_popups
        WHERE active = TRUE AND deleted_flag = FALSE
          AND (start_date IS NULL OR start_date <= NOW())
          AND (end_date IS NULL OR end_date >= NOW())
        ORDER BY priority ASC;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_popups_list(
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM homepage_popups WHERE deleted_flag = FALSE ORDER BY priority ASC;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_popups_toggle_active(
    IN p_id BIGINT,
    IN p_modified_by VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE homepage_popups
    SET active = NOT active, last_modified_by = p_modified_by, updated_at = NOW()
    WHERE id = p_id;
END;
$$;
