export default function TaskItem({ title, description, completed, finishDate, goalTitle, onToggleComplete, onEdit, onDelete }) {
  const statusClass = completed ? " is-complete" : "";
  const statusLabel = completed ? "done" : "open";

  return (
    <div className={`task-row${statusClass}`}>
      <div>
        <div className="task-title">{title || "Untitled task"}</div>
        {description ? <div className="task-description">{description}</div> : null}
        <div className="task-meta">
          Status: {statusLabel}
          {goalTitle ? ` · Goal: ${goalTitle}` : ""}
          {finishDate ? ` · Due: ${finishDate}` : ""}
        </div>
      </div>
      <div className="item-actions">
        <button className="ghost-button" type="button" onClick={onToggleComplete}>
          {completed ? "Mark open" : "Mark done"}
        </button>
        <button className="ghost-button" type="button" onClick={onEdit}>
          Edit
        </button>
        <button className="ghost-button" type="button" onClick={onDelete}>
          Delete
        </button>
      </div>
    </div>
  );
}
