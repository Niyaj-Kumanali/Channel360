CREATE OR REPLACE PROCEDURE sp_workflow_save(
    INOUT p_id BIGINT,
    IN p_name VARCHAR,
    IN p_description VARCHAR,
    IN p_module VARCHAR,
    IN p_active BOOLEAN,
    IN p_user VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_id IS NULL THEN
        INSERT INTO approval_workflows (name, description, module, active, created_by, created_at, updated_at)
        VALUES (p_name, p_description, p_module, COALESCE(p_active, TRUE), p_user, NOW(), NOW())
        RETURNING id INTO p_id;
    ELSE
        UPDATE approval_workflows
        SET name = COALESCE(p_name, name),
            description = COALESCE(p_description, description),
            module = COALESCE(p_module, module),
            active = COALESCE(p_active, active),
            updated_by = p_user,
            updated_at = NOW()
        WHERE id = p_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_workflow_delete(
    IN p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE approval_workflows SET deleted_flag = TRUE, updated_at = NOW() WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_workflow_step_save(
    INOUT p_id BIGINT,
    IN p_workflow_id BIGINT,
    IN p_step_order INTEGER,
    IN p_role_name VARCHAR,
    IN p_label VARCHAR,
    IN p_mandatory BOOLEAN,
    IN p_sla_hours INTEGER,
    IN p_escalation_role VARCHAR,
    IN p_description VARCHAR,
    IN p_user VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_id IS NULL THEN
        INSERT INTO approval_workflow_steps (workflow_id, step_order, role_name, label, mandatory, sla_hours, escalation_role, description, created_by, created_at, updated_at)
        VALUES (p_workflow_id, p_step_order, p_role_name, p_label, COALESCE(p_mandatory, TRUE), p_sla_hours, p_escalation_role, p_description, p_user, NOW(), NOW())
        RETURNING id INTO p_id;
    ELSE
        UPDATE approval_workflow_steps
        SET step_order = COALESCE(p_step_order, step_order),
            role_name = COALESCE(p_role_name, role_name),
            label = COALESCE(p_label, label),
            mandatory = COALESCE(p_mandatory, mandatory),
            sla_hours = COALESCE(p_sla_hours, sla_hours),
            escalation_role = COALESCE(p_escalation_role, escalation_role),
            description = COALESCE(p_description, description),
            updated_by = p_user,
            updated_at = NOW()
        WHERE id = p_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_workflow_step_delete(
    IN p_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE approval_workflow_steps SET deleted_flag = TRUE, updated_at = NOW() WHERE id = p_id;
END;
$$;
