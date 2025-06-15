CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type_id VARCHAR(36) NOT NULL,
    event_type_name VARCHAR(255) NOT NULL,
    tt TIMESTAMP NOT NULL,
    vt TIMESTAMP NOT NULL,
    schema_version_id VARCHAR(36) NOT NULL,
    schema_version_name VARCHAR(255) NOT NULL,
    producer_name VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL
);