CREATE TABLE IF NOT EXISTS selfaxiom.tasks (
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  goal_id BIGINT NOT NULL,
  task VARCHAR(255) NOT NULL,
  finish_date DATE NOT NULL,
  completed BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT pk_tasks PRIMARY KEY (id),
  CONSTRAINT fk_tasks_goal_id FOREIGN KEY (goal_id) REFERENCES selfaxiom.goals (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_tasks_goal_id ON selfaxiom.tasks (goal_id);
