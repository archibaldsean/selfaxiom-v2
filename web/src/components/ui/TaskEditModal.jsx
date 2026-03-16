import { useEffect, useState } from "react";
import ModalShell from "./ModalShell";

export default function TaskEditModal({ task, onClose, onSave }) {
  const [taskText, setTaskText] = useState("");
  const [description, setDescription] = useState("");
  const [finishDate, setFinishDate] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    setTaskText(task?.task || "");
    setDescription(task?.description || "");
    setFinishDate(task?.finishDate || "");
  }, [task]);

  async function handleSubmit(event) {
    event.preventDefault();
    setBusy(true);
    setError("");

    try {
      await onSave({
        task: taskText.trim(),
        description: description.trim(),
        finishDate: finishDate.trim(),
      });
    } catch (caughtError) {
      setError(caughtError.message || "Failed to save task.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <ModalShell title="Edit task" onClose={onClose}>
      <form className="modal-form" onSubmit={handleSubmit}>
        <label className="inline-form-label" htmlFor="edit-task-title">
          Task
        </label>
        <input
          id="edit-task-title"
          className="inline-form-input"
          value={taskText}
          onChange={(event) => setTaskText(event.target.value)}
          required
        />

        <label className="inline-form-label" htmlFor="edit-task-date">
          Finish date
        </label>
        <input
          id="edit-task-date"
          type="date"
          className="inline-form-input"
          value={finishDate}
          onChange={(event) => setFinishDate(event.target.value)}
          required
        />

        <label className="inline-form-label" htmlFor="edit-task-description">
          Description
        </label>
        <textarea
          id="edit-task-description"
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
