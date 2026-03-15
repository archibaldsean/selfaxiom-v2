CREATE TABLE IF NOT EXISTS selfaxiom.goals (
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  user_id BIGINT NOT NULL,
  goal VARCHAR(255) NOT NULL,
  finish_date DATE NOT NULL,
  completed BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT pk_goals PRIMARY KEY (id),
  CONSTRAINT fk_goals_user_id FOREIGN KEY (user_id) REFERENCES selfaxiom.users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_goals_user_id ON selfaxiom.goals (user_id);
