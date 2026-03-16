export default function GoalItem({ title, description, completed, finishDate, taskCount, completedTaskCount, onViewTasks, onEdit, onDelete }) {
  const progress = taskCount > 0 ? Math.round((completedTaskCount / taskCount) * 100) : 0;

  return (
    <div className="goal-card">
      <div className="goal-meta">
        Status: {completed ? "done" : "active"}
        {finishDate ? ` · Target: ${finishDate}` : ""}
      </div>
      <h3>{title || "Untitled goal"}</h3>
      {description ? <div className="goal-description">{description}</div> : null}
      <div className="progress">
        <div className="progress-bar" style={{ width: `${progress}%` }} />
      </div>
      <div className="goal-next">Tasks complete: {completedTaskCount}/{taskCount}</div>
      <div className="item-actions">
        <button className="ghost-button" type="button" onClick={onViewTasks}>
          View tasks
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
