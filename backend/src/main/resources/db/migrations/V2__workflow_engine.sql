CREATE TABLE IF NOT EXISTS workflows (
    id bigserial PRIMARY KEY,
    name varchar(255) NOT NULL,
    description text,
    active boolean NOT NULL DEFAULT true,
    metadata text,
    created_by varchar(255),
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by varchar(255),
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workflow_versions (
    id bigserial PRIMARY KEY,
    workflow_id bigint NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    version_number integer NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'DRAFT',
    graph_json jsonb,
    entity_version bigint NOT NULL DEFAULT 0,
    created_by varchar(255),
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workflow_nodes (
    id bigserial PRIMARY KEY,
    node_uuid uuid NOT NULL UNIQUE,
    workflow_version_id bigint NOT NULL REFERENCES workflow_versions(id) ON DELETE CASCADE,
    name varchar(255) NOT NULL,
    type varchar(20) NOT NULL,
    terminal_type varchar(20),
    label varchar(255),
    description text,
    entity_version bigint NOT NULL DEFAULT 0,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workflow_transitions (
    id bigserial PRIMARY KEY,
    transition_uuid uuid NOT NULL UNIQUE,
    source_node_id bigint NOT NULL REFERENCES workflow_nodes(id) ON DELETE CASCADE,
    target_node_id bigint NOT NULL REFERENCES workflow_nodes(id) ON DELETE CASCADE,
    action varchar(20) NOT NULL,
    label varchar(255),
    priority integer NOT NULL DEFAULT 0,
    entity_version bigint NOT NULL DEFAULT 0,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS node_assignments (
    id bigserial PRIMARY KEY,
    assignment_uuid uuid NOT NULL UNIQUE,
    node_id bigint NOT NULL UNIQUE REFERENCES workflow_nodes(id) ON DELETE CASCADE,
    policy varchar(30) NOT NULL,
    required_approval_count integer,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS approver_rules (
    id bigserial PRIMARY KEY,
    rule_uuid uuid NOT NULL UNIQUE,
    assignment_id bigint NOT NULL REFERENCES node_assignments(id) ON DELETE CASCADE,
    approver_type varchar(30) NOT NULL,
    role_name varchar(100),
    user_id bigint,
    region_id bigint,
    department varchar(100),
    dynamic_provider varchar(200),
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS condition_expressions (
    id bigserial PRIMARY KEY,
    condition_uuid uuid NOT NULL UNIQUE,
    transition_id bigint REFERENCES workflow_transitions(id) ON DELETE CASCADE,
    parent_id bigint REFERENCES condition_expressions(id) ON DELETE CASCADE,
    type varchar(10) NOT NULL,
    operator varchar(5),
    field varchar(100),
    op varchar(20),
    value text,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workflow_requests (
    id bigserial PRIMARY KEY,
    workflow_version_id bigint NOT NULL REFERENCES workflow_versions(id),
    current_node_id bigint REFERENCES workflow_nodes(id),
    request_type varchar(100) NOT NULL,
    request_reference_id bigint,
    requestor_id bigint NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    metadata_json jsonb,
    idempotency_key varchar(100) UNIQUE,
    entity_version bigint NOT NULL DEFAULT 0,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workflow_tasks (
    id bigserial PRIMARY KEY,
    request_id bigint NOT NULL REFERENCES workflow_requests(id) ON DELETE CASCADE,
    node_id bigint NOT NULL REFERENCES workflow_nodes(id),
    assigned_user_id bigint,
    assigned_role_id bigint,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    acted_by bigint,
    acted_at timestamp,
    comments text,
    entity_version bigint NOT NULL DEFAULT 0,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workflow_history (
    id bigserial PRIMARY KEY,
    request_id bigint NOT NULL REFERENCES workflow_requests(id) ON DELETE CASCADE,
    from_node_id bigint REFERENCES workflow_nodes(id),
    to_node_id bigint REFERENCES workflow_nodes(id),
    action varchar(20) NOT NULL,
    actor_id bigint,
    comments text,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workflow_outbox (
    id bigserial PRIMARY KEY,
    aggregate_type varchar(50) NOT NULL,
    aggregate_id bigint NOT NULL,
    event_type varchar(100) NOT NULL,
    payload text NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    retry_count integer NOT NULL DEFAULT 0,
    next_retry_at timestamp,
    last_error text,
    published_at timestamp,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS segments (
    id bigserial PRIMARY KEY,
    name varchar(100) NOT NULL UNIQUE,
    code varchar(50) UNIQUE,
    description text,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS business_processes (
    id bigserial PRIMARY KEY,
    name varchar(100) NOT NULL UNIQUE,
    code varchar(50) UNIQUE,
    description text,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS segment_process_mappings (
    id bigserial PRIMARY KEY,
    segment_id bigint NOT NULL REFERENCES segments(id) ON DELETE CASCADE,
    business_process_id bigint NOT NULL REFERENCES business_processes(id) ON DELETE CASCADE,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(segment_id, business_process_id)
);

CREATE INDEX IF NOT EXISTS idx_workflow_versions_workflow_id ON workflow_versions(workflow_id);
CREATE INDEX IF NOT EXISTS idx_workflow_versions_status ON workflow_versions(status);
CREATE INDEX IF NOT EXISTS idx_workflow_nodes_workflow_version_id ON workflow_nodes(workflow_version_id);
CREATE INDEX IF NOT EXISTS idx_workflow_nodes_node_uuid ON workflow_nodes(node_uuid);
CREATE INDEX IF NOT EXISTS idx_workflow_transitions_source_node_id ON workflow_transitions(source_node_id);
CREATE INDEX IF NOT EXISTS idx_workflow_transitions_target_node_id ON workflow_transitions(target_node_id);
CREATE INDEX IF NOT EXISTS idx_workflow_transitions_transition_uuid ON workflow_transitions(transition_uuid);
CREATE INDEX IF NOT EXISTS idx_approver_rules_assignment_id ON approver_rules(assignment_id);
CREATE INDEX IF NOT EXISTS idx_condition_expressions_transition_id ON condition_expressions(transition_id);
CREATE INDEX IF NOT EXISTS idx_condition_expressions_parent_id ON condition_expressions(parent_id);
CREATE INDEX IF NOT EXISTS idx_workflow_requests_version_id ON workflow_requests(workflow_version_id);
CREATE INDEX IF NOT EXISTS idx_workflow_requests_requestor_id ON workflow_requests(requestor_id);
CREATE INDEX IF NOT EXISTS idx_workflow_requests_status ON workflow_requests(status);
CREATE INDEX IF NOT EXISTS idx_workflow_tasks_request_id ON workflow_tasks(request_id);
CREATE INDEX IF NOT EXISTS idx_workflow_tasks_assigned_user_id ON workflow_tasks(assigned_user_id);
CREATE INDEX IF NOT EXISTS idx_workflow_tasks_status ON workflow_tasks(status);
CREATE INDEX IF NOT EXISTS idx_workflow_history_request_id ON workflow_history(request_id);
CREATE INDEX IF NOT EXISTS idx_workflow_outbox_status ON workflow_outbox(status);
