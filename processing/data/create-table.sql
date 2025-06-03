CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    event_type_id UUID NOT NULL,
    event_type_name VARCHAR(255) NOT NULL,
    tt TIMESTAMPTZ NOT NULL,
    vt TIMESTAMPTZ NOT NULL,
    schema_version_id UUID NOT NULL,
    schema_version_name VARCHAR(255) NOT NULL,
    producer_name VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL
);