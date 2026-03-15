export default function TaskItem({ title, completed, finishDate, goalTitle }) {
  const statusClass = completed ? " is-complete" : "";
  const statusLabel = completed ? "done" : "open";

  return (
    <div className={`task-row${statusClass}`}>
      <div>
        <div className="task-title">{title || "Untitled task"}</div>
        <div className="task-meta">
          Status: {statusLabel}
          {goalTitle ? ` · Goal: ${goalTitle}` : ""}
          {finishDate ? ` · Due: ${finishDate}` : ""}
        </div>
      </div>
    </div>
  );
}
