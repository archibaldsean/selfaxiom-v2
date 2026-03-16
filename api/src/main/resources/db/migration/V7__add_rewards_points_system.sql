ALTER TABLE selfaxiom.users
ADD COLUMN IF NOT EXISTS points_balance INTEGER NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS selfaxiom.reward_events (
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  user_id BIGINT NOT NULL,
  goal_id BIGINT,
  task_id BIGINT,
  event_type VARCHAR(64) NOT NULL,
  points_delta INTEGER NOT NULL,
  balance_after INTEGER NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT pk_reward_events PRIMARY KEY (id),
  CONSTRAINT fk_reward_events_user_id FOREIGN KEY (user_id) REFERENCES selfaxiom.users (id) ON DELETE CASCADE,
  CONSTRAINT fk_reward_events_goal_id FOREIGN KEY (goal_id) REFERENCES selfaxiom.goals (id) ON DELETE SET NULL,
  CONSTRAINT fk_reward_events_task_id FOREIGN KEY (task_id) REFERENCES selfaxiom.tasks (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_reward_events_user_id_created_at
ON selfaxiom.reward_events (user_id, created_at DESC);
