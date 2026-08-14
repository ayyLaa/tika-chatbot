-- ekstenzija 
CREATE EXTENSION IF NOT EXISTS vector;

-- USERS
CREATE TABLE users (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name           VARCHAR(150) NOT NULL,
    username            VARCHAR(100) NOT NULL UNIQUE,
    email               VARCHAR(255) UNIQUE NOT NULL,
    phone_number        VARCHAR(30),
    password_hash       VARCHAR(255) NOT NULL,
    department          VARCHAR(100),
    user_role           VARCHAR(20) NOT NULL DEFAULT 'user',
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    email_verified      BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at          TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT check_user_role CHECK (user_role IN ('admin', 'user'))
);

-- DOCUMENTS
CREATE TABLE documents (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    file_name       VARCHAR(255) NOT NULL,
    doc_type        VARCHAR(20) NOT NULL,
    doc_path        VARCHAR(500) NOT NULL,
    uploaded_by     UUID REFERENCES users(id),
    doc_status      VARCHAR(20) NOT NULL DEFAULT 'pending',
    uploaded_at     TIMESTAMP NOT NULL DEFAULT now(),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- CHUNKS (pgvector)
CREATE TABLE chunks (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id     UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    chunk_index     INT NOT NULL,
    chunk_text      TEXT NOT NULL,
    embedding       vector(768),
    source_page     INT,
    source_url      VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT unique_chunk_per_doc UNIQUE (document_id, chunk_index)
);

-- CHAT SESSIONS
CREATE TABLE chat_sessions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    title           VARCHAR(255),
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- MESSAGES
CREATE TABLE messages (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id      UUID NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
    question        TEXT NOT NULL,
    answer          TEXT,
    response_time_ms INT,
    tokens_used     INT,
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- MESSAGE_SOURCES
CREATE TABLE message_sources (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id      UUID NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    chunk_id        UUID NOT NULL REFERENCES chunks(id),
    relevance_score FLOAT
);

-- LOGIN HISTORY
CREATE TABLE login_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    type_login      VARCHAR(20) NOT NULL,
    ip_address      VARCHAR(45),
    date_time       TIMESTAMP NOT NULL DEFAULT now()
);

-- MESSAGE FEEDBACK
CREATE TABLE message_feedback (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id      UUID NOT NULL REFERENCES messages(id),
    rating          SMALLINT,
    comment         TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- AUDIT LOG
CREATE TABLE audit_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admin_id        UUID NOT NULL REFERENCES users(id),
    admin_action    VARCHAR(100) NOT NULL,
    target_user_id  UUID REFERENCES users(id),
    date_time       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE allowed_web_sources (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    url              VARCHAR(500) NOT NULL,
    domain           VARCHAR(255) NOT NULL,
    description      VARCHAR(255),
    last_scraped_at  TIMESTAMP,
    scrape_frequency VARCHAR(20) DEFAULT 'manual',
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    added_by         UUID REFERENCES users(id),
    created_at       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE user_invites (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) UNIQUE NOT NULL,
    invited_role    VARCHAR(20) NOT NULL DEFAULT 'user',
    invite_token    VARCHAR(255) UNIQUE NOT NULL,
    invited_by      UUID NOT NULL REFERENCES users(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'pending',
    expires_at      TIMESTAMP NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT check_invited_role CHECK (invited_role IN ('admin', 'user')),
    CONSTRAINT check_invite_status CHECK (status IN ('pending', 'accepted', 'expired'))
);

CREATE TABLE password_reset_requests (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'pending',
    requested_at    TIMESTAMP NOT NULL DEFAULT now(),
    reviewed_by     UUID REFERENCES users(id),
    reviewed_at     TIMESTAMP,
    reset_token     VARCHAR(255),
    token_expires   TIMESTAMP,
    CONSTRAINT check_reset_status CHECK (status IN ('pending', 'approved', 'rejected', 'used'))
);

-- INDEKSI
CREATE INDEX idx_chunks_document_id ON chunks(document_id);
CREATE INDEX idx_messages_session_id ON messages(session_id);
CREATE INDEX idx_chat_sessions_user_id ON chat_sessions(user_id);
CREATE INDEX idx_login_history_user_id ON login_history(user_id);
CREATE INDEX idx_documents_status ON documents(doc_status);
CREATE INDEX idx_message_sources_message_id ON message_sources(message_id);
CREATE INDEX idx_message_sources_chunk_id ON message_sources(chunk_id);
CREATE INDEX idx_message_feedback_message_id ON message_feedback(message_id);
CREATE INDEX idx_audit_log_admin_id ON audit_log(admin_id);
CREATE INDEX idx_messages_session_created ON messages(session_id, created_at);
CREATE INDEX idx_user_invites_email ON user_invites(email);
CREATE INDEX idx_user_invites_token ON user_invites(invite_token);
CREATE INDEX idx_password_reset_user_id ON password_reset_requests(user_id);
CREATE INDEX idx_password_reset_status ON password_reset_requests(status);

-- DEFAULT ADMIN ACCOUNT (Password: admin123)
INSERT INTO users (full_name, username, email, password_hash, department, user_role, is_active, email_verified)
VALUES (
    'Sistem Yoneticisi', 
    'admin', 
    'admin@tika.gov.tr', 
    '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 
    'IT', 
    'admin', 
    true, 
    true
);