export default function GoalItem({ title, completed, finishDate, taskCount, completedTaskCount }) {
  const progress = taskCount > 0 ? Math.round((completedTaskCount / taskCount) * 100) : 0;

  return (
    <div className="goal-card">
      <div className="goal-meta">
        Status: {completed ? "done" : "active"}
        {finishDate ? ` · Target: ${finishDate}` : ""}
      </div>
      <h3>{title || "Untitled goal"}</h3>
      <div className="progress">
        <div className="progress-bar" style={{ width: `${progress}%` }} />
      </div>
      <div className="goal-next">Tasks complete: {completedTaskCount}/{taskCount}</div>
    </div>
  );
}
