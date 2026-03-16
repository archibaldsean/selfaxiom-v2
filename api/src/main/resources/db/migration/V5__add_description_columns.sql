ALTER TABLE selfaxiom.goals
ADD COLUMN IF NOT EXISTS description TEXT;

ALTER TABLE selfaxiom.tasks
ADD COLUMN IF NOT EXISTS description TEXT;
