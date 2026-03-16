import { useEffect, useMemo, useState } from "react";
import { fetchGoals, fetchTasksByGoal } from "../lib/api";

function classifyTasks(tasks) {
  const today = new Date().toISOString().slice(0, 10);
  const buckets = { today: [], overdue: [], upcoming: [] };

  tasks.forEach((task) => {
    if (!task.finishDate) return;
    if (task.finishDate === today) buckets.today.push(task);
    else if (task.finishDate < today) buckets.overdue.push(task);
    else buckets.upcoming.push(task);
  });

  return buckets;
}

export default function DashboardPage() {
  const [goals, setGoals] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function loadData() {
      setLoading(true);
      setError("");

      try {
        const goalList = await fetchGoals();
        const nestedTasks = await Promise.all(goalList.map((goal) => fetchTasksByGoal(goal.id)));
        const flatTasks = nestedTasks.flat().map((task) => {
          const goalTitle = goalList.find((goal) => goal.id === task.goalId)?.goal || "";
          return { ...task, goalTitle };
        });

        if (!cancelled) {
          setGoals(goalList);
          setTasks(flatTasks);
        }
      } catch (caughtError) {
        if (!cancelled) setError(caughtError.message || "Failed to load dashboard.");
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    loadData();
    return () => {
      cancelled = true;
    };
  }, []);

  const grouped = useMemo(() => classifyTasks(tasks), [tasks]);
  const streak = useMemo(() => tasks.filter((task) => task.completed).length, [tasks]);

  return (
    <div className="dashboard page">
      <section className="dashboard-hero">
        <div>
          <div className="dashboard-kicker">Today</div>
          <h1>Execute the plan. No drift.</h1>
          <p className="dashboard-intention">Daily intention: Ship one meaningful step toward the obsession.</p>
        </div>
        <div className="dashboard-message">Completed streak units: {streak}</div>
      </section>

      {error ? <div className="inline-form-error">{error}</div> : null}

      <section className="dashboard-tasks">
        <div className="section-title">Task Focus</div>
        {loading ? <div className="empty-state">Loading...</div> : null}

        {!loading ? (
          <>
            <div className="dashboard-task-group">
              <div className="dashboard-task-title">Today</div>
              {grouped.today.length === 0 ? (
                <div className="empty-state">No tasks scheduled for today.</div>
              ) : (
                <ul>
                  {grouped.today.map((task) => (
                    <li key={task.id}>
                      <div className="task-title">{task.task}</div>
                      <div className="task-meta">{task.goalTitle ? `Goal: ${task.goalTitle}` : "No goal"}</div>
                    </li>
                  ))}
                </ul>
              )}
            </div>

            <div className="dashboard-task-group">
              <div className="dashboard-task-title">Overdue</div>
              {grouped.overdue.length === 0 ? (
                <div className="empty-state">No overdue tasks.</div>
              ) : (
                <ul>
                  {grouped.overdue.map((task) => (
                    <li key={task.id}>
                      <div className="task-title">{task.task}</div>
                      <div className="task-meta">
                        {task.goalTitle ? `Goal: ${task.goalTitle}` : "No goal"} · Due: {task.finishDate}
                      </div>
                    </li>
                  ))}
                </ul>
              )}
            </div>

            <div className="dashboard-task-group">
              <div className="dashboard-task-title">Upcoming</div>
              {grouped.upcoming.length === 0 ? (
                <div className="empty-state">No upcoming tasks.</div>
              ) : (
                <ul>
                  {grouped.upcoming.map((task) => (
                    <li key={task.id}>
                      <div className="task-title">{task.task}</div>
                      <div className="task-meta">
                        {task.goalTitle ? `Goal: ${task.goalTitle}` : "No goal"} · Due: {task.finishDate}
                      </div>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </>
        ) : null}
      </section>

      <section className="dashboard-goals">
        <div className="section-title">Active Goals</div>
        <div className="goal-grid">
          {!loading && goals.length === 0 ? <div className="empty-state">No active goals yet.</div> : null}
          {goals.map((goal) => {
            const goalTasks = tasks.filter((task) => task.goalId === goal.id);
            const completedCount = goalTasks.filter((task) => task.completed).length;
            const progress = goalTasks.length > 0 ? Math.round((completedCount / goalTasks.length) * 100) : 0;

            return (
              <div className="goal-card" key={goal.id}>
                <div className="goal-meta">
                  Status: {goal.completed ? "done" : "active"}
                  {goal.finishDate ? ` · Target: ${goal.finishDate}` : ""}
                </div>
                <h3>{goal.goal}</h3>
                <div className="progress">
                  <div className="progress-bar" style={{ width: `${progress}%` }} />
                </div>
                <div className="goal-next">Progress: {progress}%</div>
              </div>
            );
          })}
        </div>
      </section>
    </div>
  );
}
