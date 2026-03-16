UPDATE selfaxiom.users
SET points_balance = 0
WHERE points_balance < 0;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'chk_users_points_balance_non_negative'
  ) THEN
    ALTER TABLE selfaxiom.users
    ADD CONSTRAINT chk_users_points_balance_non_negative CHECK (points_balance >= 0);
  END IF;
END $$;
