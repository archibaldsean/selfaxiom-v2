WITH duplicate_task_events AS (
  SELECT id, user_id, points_delta,
    ROW_NUMBER() OVER (PARTITION BY user_id, task_id, event_type ORDER BY id) AS row_num
  FROM selfaxiom.reward_events
  WHERE task_id IS NOT NULL
),
removed_task_events AS (
  DELETE FROM selfaxiom.reward_events reward_events
  USING duplicate_task_events duplicate_task_events
  WHERE reward_events.id = duplicate_task_events.id
    AND duplicate_task_events.row_num > 1
  RETURNING reward_events.user_id, reward_events.points_delta
)
UPDATE selfaxiom.users users
SET points_balance = GREATEST(0, users.points_balance - removed.points_to_remove)
FROM (
  SELECT user_id, COALESCE(SUM(points_delta), 0) AS points_to_remove
  FROM removed_task_events
  GROUP BY user_id
) removed
WHERE users.id = removed.user_id;

WITH duplicate_goal_events AS (
  SELECT id, user_id, points_delta,
    ROW_NUMBER() OVER (PARTITION BY user_id, goal_id, event_type ORDER BY id) AS row_num
  FROM selfaxiom.reward_events
  WHERE goal_id IS NOT NULL
),
removed_goal_events AS (
  DELETE FROM selfaxiom.reward_events reward_events
  USING duplicate_goal_events duplicate_goal_events
  WHERE reward_events.id = duplicate_goal_events.id
    AND duplicate_goal_events.row_num > 1
  RETURNING reward_events.user_id, reward_events.points_delta
)
UPDATE selfaxiom.users users
SET points_balance = GREATEST(0, users.points_balance - removed.points_to_remove)
FROM (
  SELECT user_id, COALESCE(SUM(points_delta), 0) AS points_to_remove
  FROM removed_goal_events
  GROUP BY user_id
) removed
WHERE users.id = removed.user_id;

CREATE UNIQUE INDEX IF NOT EXISTS ux_reward_events_task_event_once
ON selfaxiom.reward_events (user_id, task_id, event_type)
WHERE task_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_reward_events_goal_event_once
ON selfaxiom.reward_events (user_id, goal_id, event_type)
WHERE goal_id IS NOT NULL;
