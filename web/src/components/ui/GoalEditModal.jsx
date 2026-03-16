import { useEffect, useState } from "react";
import ModalShell from "./ModalShell";

export default function GoalEditModal({ goal, onClose, onSave }) {
  const [goalText, setGoalText] = useState("");
  const [description, setDescription] = useState("");
  const [finishDate, setFinishDate] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    setGoalText(goal?.goal || "");
    setDescription(goal?.description || "");
    setFinishDate(goal?.finishDate || "");
  }, [goal]);

  async function handleSubmit(event) {
    event.preventDefault();
    setBusy(true);
    setError("");

    try {
      await onSave({
        goal: goalText.trim(),
        description: description.trim(),
        finishDate: finishDate.trim(),
      });
    } catch (caughtError) {
      setError(caughtError.message || "Failed to save goal.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <ModalShell title="Edit goal" onClose={onClose}>
      <form className="modal-form" onSubmit={handleSubmit}>
        <label className="inline-form-label" htmlFor="edit-goal-title">
          Goal
        </label>
        <input
          id="edit-goal-title"
          className="inline-form-input"
          value={goalText}
          onChange={(event) => setGoalText(event.target.value)}
          required
        />

        <label className="inline-form-label" htmlFor="edit-goal-date">
          Finish date
        </label>
        <input
          id="edit-goal-date"
          type="date"
          className="inline-form-input"
          value={finishDate}
          onChange={(event) => setFinishDate(event.target.value)}
          required
        />

        <label className="inline-form-label" htmlFor="edit-goal-description">
          Description
        </label>
        <textarea
          id="edit-goal-description"
          rows={3}
          className="inline-form-input"
          value={description}
          onChange={(event) => setDescription(event.target.value)}
        />

        {error ? <div className="inline-form-error">{error}</div> : null}

        <div className="inline-form-actions">
          <button className="ghost-button" type="button" onClick={onClose}>
            Cancel
          </button>
          <button className="primary-button" type="submit" disabled={busy}>
            {busy ? "Saving..." : "Save"}
          </button>
        </div>
      </form>
    </ModalShell>
  );
}
