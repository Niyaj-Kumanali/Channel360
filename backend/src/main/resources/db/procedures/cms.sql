CREATE OR REPLACE PROCEDURE sp_homepage_sections_create(
    IN p_section_name VARCHAR,
    IN p_section_type VARCHAR,
    IN p_title VARCHAR,
    IN p_subtitle VARCHAR,
    IN p_description TEXT,
    IN p_image_url VARCHAR,
    IN p_button_text VARCHAR,
    IN p_button_url VARCHAR,
    IN p_display_order INTEGER,
    IN p_active BOOLEAN,
    IN p_start_date TIMESTAMP,
    IN p_end_date TIMESTAMP,
    IN p_created_by VARCHAR,
    INOUT p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO homepage_sections (section_name, section_type, title, subtitle, description, image_url, button_text, button_url, display_order, active, start_date, end_date, created_by, created_at, updated_at)
    VALUES (p_section_name, p_section_type, p_title, p_subtitle, p_description, p_image_url, p_button_text, p_button_url, p_display_order, p_active, p_start_date, p_end_date, p_created_by, NOW(), NOW())
    RETURNING id INTO p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_sections_update(
    IN p_id BIGINT,
    IN p_section_name VARCHAR,
    IN p_section_type VARCHAR,
    IN p_title VARCHAR,
    IN p_subtitle VARCHAR,
    IN p_description TEXT,
    IN p_image_url VARCHAR,
    IN p_button_text VARCHAR,
    IN p_button_url VARCHAR,
    IN p_display_order INTEGER,
    IN p_active BOOLEAN,
    IN p_start_date TIMESTAMP,
    IN p_end_date TIMESTAMP,
    IN p_modified_by VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE homepage_sections
    SET section_name = COALESCE(p_section_name, section_name),
        section_type = COALESCE(p_section_type, section_type),
        title = COALESCE(p_title, title),
        subtitle = COALESCE(p_subtitle, subtitle),
        description = COALESCE(p_description, description),
        image_url = COALESCE(p_image_url, image_url),
        button_text = COALESCE(p_button_text, button_text),
        button_url = COALESCE(p_button_url, button_url),
        display_order = COALESCE(p_display_order, display_order),
        active = COALESCE(p_active, active),
        start_date = COALESCE(p_start_date, start_date),
        end_date = COALESCE(p_end_date, end_date),
        last_modified_by = p_modified_by,
        updated_at = NOW()
    WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_sections_get_by_id(
    IN p_id BIGINT,
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM homepage_sections WHERE id = p_id AND deleted_flag = FALSE;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_sections_get_active(
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR
        SELECT * FROM homepage_sections
        WHERE active = TRUE
          AND deleted_flag = FALSE
          AND (start_date IS NULL OR start_date <= NOW())
          AND (end_date IS NULL OR end_date >= NOW())
        ORDER BY display_order ASC;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_sections_get_all(
    INOUT p_data REFCURSOR
)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM homepage_sections WHERE deleted_flag = FALSE ORDER BY display_order ASC;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_sections_delete(
    IN p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM homepage_sections WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_sections_toggle_active(
    IN p_id BIGINT,
    IN p_modified_by VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE homepage_sections
    SET active = NOT active,
        last_modified_by = p_modified_by,
        updated_at = NOW()
    WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_sections_reorder(
    IN p_section_orders JSON
)
LANGUAGE plpgsql AS $$
DECLARE
    item JSON;
BEGIN
    FOR item IN SELECT * FROM json_array_elements(p_section_orders)
    LOOP
        UPDATE homepage_sections
        SET display_order = (item->>'displayOrder')::INTEGER,
            updated_at = NOW()
        WHERE id = (item->>'id')::BIGINT;
    END LOOP;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_sections_exists(
    IN p_id BIGINT,
    OUT p_exists BOOLEAN
)
LANGUAGE plpgsql AS $$
BEGIN
    SELECT EXISTS(SELECT 1 FROM homepage_sections WHERE id = p_id AND deleted_flag = FALSE) INTO p_exists;
END;
$$;
