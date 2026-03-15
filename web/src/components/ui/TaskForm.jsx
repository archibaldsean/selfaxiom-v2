import { useState } from "react";

export default function TaskForm({ goals, defaultGoalId = "", defaultDate, onCreate }) {
  const [open, setOpen] = useState(Boolean(defaultGoalId));
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  async function onSubmit(event) {
    event.preventDefault();
    setBusy(true);
    setError("");

    const form = event.currentTarget;
    const fd = new FormData(form);
    const goalValue = Number(fd.get("goal_id"));

    if (!goalValue) {
      setError("Pick a goal first.");
      setBusy(false);
      return;
    }

    try {
      await onCreate({
        goalId: goalValue,
        task: String(fd.get("task") || "").trim(),
        finishDate: String(fd.get("finishDate") || "").trim(),
      });
      form.reset();
      setOpen(false);
    } catch (caughtError) {
      setError(caughtError.message || "Failed to create task.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="inline-form">
      <div className="inline-form-toggle">
        <button className="primary-button" type="button" onClick={() => setOpen((prev) => !prev)}>
          {open ? "Close" : "New task"}
        </button>
      </div>
      {open ? (
        <form className="inline-form-card" onSubmit={onSubmit}>
          <label className="inline-form-label" htmlFor="task-title">
            Task
          </label>
          <input id="task-title" name="task" className="inline-form-input" required />

          <label className="inline-form-label" htmlFor="task-goal">
            Goal (optional)
          </label>
          <select id="task-goal" name="goal_id" className="inline-form-input" defaultValue={defaultGoalId ?? ""}>
            <option value="">No goal</option>
            {goals.map((goal) => (
              <option key={goal.id} value={goal.id}>
                {goal.title}
              </option>
            ))}
          </select>

          <label className="inline-form-label" htmlFor="task-due">
            Finish date
          </label>
          <input
            id="task-due"
            name="finishDate"
            type="date"
            className="inline-form-input"
            defaultValue={defaultDate}
            required
          />

          {error ? <div className="inline-form-error">{error}</div> : null}

          <div className="inline-form-actions">
            <button className="ghost-button" type="button" onClick={() => setOpen(false)}>
              Cancel
            </button>
            <button className="primary-button" type="submit" disabled={busy}>
              {busy ? "Saving..." : "Create task"}
            </button>
          </div>
        </form>
      ) : null}
    </div>
  );
}
