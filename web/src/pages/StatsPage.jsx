import { useEffect, useMemo, useState } from "react";
import { fetchGoals, fetchTasksByGoal } from "../lib/api";

export default function StatsPage() {
  const [goals, setGoals] = useState([]);
  const [tasks, setTasks] = useState([]);

  useEffect(() => {
    async function loadStats() {
      const goalList = await fetchGoals();
      const taskList = (await Promise.all(goalList.map((goal) => fetchTasksByGoal(goal.id)))).flat();
      setGoals(goalList);
      setTasks(taskList);
    }

    loadStats().catch(() => {
      setGoals([]);
      setTasks([]);
    });
  }, []);

  const completedTasks = useMemo(() => tasks.filter((task) => task.completed).length, [tasks]);
  const completionRate = useMemo(() => {
    if (tasks.length === 0) return 0;
    return Math.round((completedTasks / tasks.length) * 100);
  }, [tasks, completedTasks]);

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Stats / History</h1>
          <p>Cold facts. No excuses.</p>
        </div>
      </header>

      <section className="stats-grid">
        <div className="stat-card">
          <div className="stat-label">Goals</div>
          <div className="stat-value">{goals.length}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Tasks</div>
          <div className="stat-value">{tasks.length}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Completion rate</div>
          <div className="stat-value">{completionRate}%</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Completed</div>
          <div className="stat-value">{completedTasks}</div>
        </div>
      </section>

      <section className="stats-panels">
        <div className="stats-panel">
          <div className="section-title">Streak history</div>
          <div className="momentum-placeholder" />
        </div>
        <div className="stats-panel">
          <div className="section-title">Completion rates</div>
          <div className="momentum-placeholder" />
        </div>
      </section>
    </div>
  );
}
