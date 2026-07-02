CREATE TABLE IF NOT EXISTS app_log (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    level VARCHAR(20) NOT NULL,
    category VARCHAR(80) NOT NULL,
    action VARCHAR(255) NOT NULL,
    method VARCHAR(12),
    path VARCHAR(500),
    query_string TEXT,
    status_code INTEGER,
    duration_ms BIGINT,
    user_id BIGINT,
    username VARCHAR(255),
    user_role VARCHAR(30),
    ip_address VARCHAR(120),
    user_agent VARCHAR(500),
    details TEXT,
    error_message TEXT
);

CREATE INDEX IF NOT EXISTS idx_app_log_created_at ON app_log(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_app_log_level_created_at ON app_log(level, created_at DESC);
