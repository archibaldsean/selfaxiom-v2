import { useState } from "react";

export default function GoalForm({ onCreate, defaultDate }) {
  const [open, setOpen] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  async function onSubmit(event) {
    event.preventDefault();
    setBusy(true);
    setError("");

    const form = event.currentTarget;
    const fd = new FormData(form);

    try {
      await onCreate({
        goal: String(fd.get("goal") || "").trim(),
        finishDate: String(fd.get("finishDate") || "").trim(),
      });
      form.reset();
      setOpen(false);
    } catch (caughtError) {
      setError(caughtError.message || "Failed to create goal.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="inline-form">
      <div className="inline-form-toggle">
        <button className="primary-button" type="button" onClick={() => setOpen((prev) => !prev)}>
          {open ? "Close" : "New goal"}
        </button>
      </div>
      {open ? (
        <form className="inline-form-card" onSubmit={onSubmit}>
          <label className="inline-form-label" htmlFor="goal-title">
            Goal
          </label>
          <input id="goal-title" name="goal" className="inline-form-input" required />

          <label className="inline-form-label" htmlFor="goal-target">
            Finish date
          </label>
          <input
            id="goal-target"
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
              {busy ? "Saving..." : "Create goal"}
            </button>
          </div>
        </form>
      ) : null}
    </div>
  );
}
