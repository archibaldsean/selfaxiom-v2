CREATE TABLE IF NOT EXISTS selfaxiom.refresh_sessions (
  id UUID PRIMARY KEY,
  user_id BIGINT NOT NULL,
  token_hash VARCHAR(64) NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  revoked_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_refresh_sessions_user_id FOREIGN KEY (user_id) REFERENCES selfaxiom.users (id) ON DELETE CASCADE,
  CONSTRAINT uq_refresh_sessions_token_hash UNIQUE (token_hash)
);

CREATE INDEX IF NOT EXISTS idx_refresh_sessions_user_id ON selfaxiom.refresh_sessions (user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_sessions_expires_at ON selfaxiom.refresh_sessions (expires_at);
