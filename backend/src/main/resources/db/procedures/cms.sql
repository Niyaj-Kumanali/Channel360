CREATE OR REPLACE PROCEDURE sp_homepage_section_save(
    INOUT p_id BIGINT,
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
    IN p_user VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_id IS NULL THEN
        INSERT INTO homepage_sections (section_name, section_type, title, subtitle, description, image_url, button_text, button_url, display_order, active, start_date, end_date, created_by, created_at, updated_at)
        VALUES (p_section_name, p_section_type, p_title, p_subtitle, p_description, p_image_url, p_button_text, p_button_url, COALESCE(p_display_order, 0), COALESCE(p_active, TRUE), p_start_date, p_end_date, p_user, NOW(), NOW())
        RETURNING id INTO p_id;
    ELSE
        UPDATE homepage_sections
        SET section_name = COALESCE(p_section_name, section_name),
            section_type = COALESCE(p_section_type, section_type),
            title = COALESCE(p_title, title),
            subtitle = p_subtitle,
            description = p_description,
            image_url = p_image_url,
            button_text = p_button_text,
            button_url = p_button_url,
            display_order = COALESCE(p_display_order, display_order),
            active = COALESCE(p_active, active),
            start_date = p_start_date,
            end_date = p_end_date,
            updated_by = p_user,
            updated_at = NOW()
        WHERE id = p_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_section_delete(IN p_id BIGINT)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE homepage_sections SET deleted_flag = TRUE, updated_at = NOW() WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_section_get(IN p_id BIGINT, INOUT p_data REFCURSOR)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM homepage_sections WHERE id = p_id AND deleted_flag = FALSE;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_sections_list(INOUT p_data REFCURSOR)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM homepage_sections WHERE deleted_flag = FALSE ORDER BY display_order ASC, id DESC;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_sections_published(INOUT p_data REFCURSOR)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM homepage_sections
        WHERE deleted_flag = FALSE AND active = TRUE
          AND (start_date IS NULL OR start_date <= NOW())
          AND (end_date IS NULL OR end_date >= NOW())
        ORDER BY display_order ASC, id DESC;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_popup_save(
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
    IN p_user VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_id IS NULL THEN
        INSERT INTO homepage_popups (title, description, image_url, cta_button_text, cta_url, priority, active, start_date, end_date, created_by, created_at, updated_at)
        VALUES (p_title, p_description, p_image_url, p_cta_button_text, p_cta_url, COALESCE(p_priority, 0), COALESCE(p_active, TRUE), p_start_date, p_end_date, p_user, NOW(), NOW())
        RETURNING id INTO p_id;
    ELSE
        UPDATE homepage_popups
        SET title = COALESCE(p_title, title),
            description = p_description,
            image_url = p_image_url,
            cta_button_text = p_cta_button_text,
            cta_url = p_cta_url,
            priority = COALESCE(p_priority, priority),
            active = COALESCE(p_active, active),
            start_date = p_start_date,
            end_date = p_end_date,
            updated_by = p_user,
            updated_at = NOW()
        WHERE id = p_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_popup_delete(IN p_id BIGINT)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE homepage_popups SET deleted_flag = TRUE, updated_at = NOW() WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_popup_get(IN p_id BIGINT, INOUT p_data REFCURSOR)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM homepage_popups WHERE id = p_id AND deleted_flag = FALSE;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_popups_list(INOUT p_data REFCURSOR)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM homepage_popups WHERE deleted_flag = FALSE ORDER BY priority DESC, id DESC;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_homepage_popups_active(INOUT p_data REFCURSOR)
LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_data FOR SELECT * FROM homepage_popups
        WHERE deleted_flag = FALSE AND active = TRUE
          AND (start_date IS NULL OR start_date <= NOW())
          AND (end_date IS NULL OR end_date >= NOW())
        ORDER BY priority DESC, id DESC;
END;
$$;
