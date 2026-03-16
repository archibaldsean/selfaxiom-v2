import { useEffect, useMemo, useState } from "react";
import { fetchGoals, fetchTasksByGoal } from "../lib/api";

const DAY_MS = 24 * 60 * 60 * 1000;

function toDate(value) {
  const parsed = new Date(`${value}T00:00:00`);
  return Number.isNaN(parsed.getTime()) ? null : parsed;
}

function dateKey(date) {
  return date.toISOString().slice(0, 10);
}

function formatDate(dateString) {
  const date = toDate(dateString);
  if (!date) return "No date";
  return date.toLocaleDateString(undefined, {
    month: "short",
    day: "numeric",
    year: "numeric",
  });
}

function distanceLabel(dateString) {
  const targetDate = toDate(dateString);
  if (!targetDate) return "No date";

  const today = new Date();
  const todayMidnight = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  const deltaDays = Math.round((targetDate.getTime() - todayMidnight.getTime()) / DAY_MS);

  if (deltaDays === 0) return "Today";
  if (deltaDays === 1) return "Tomorrow";
  if (deltaDays === -1) return "Yesterday";
  if (deltaDays > 1) return `In ${deltaDays} days`;
  return `${Math.abs(deltaDays)} days ago`;
}

export default function StatsPage() {
  const [goals, setGoals] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadStats() {
      setLoading(true);
      setError("");

      try {
        const goalList = await fetchGoals();
        const taskList = (await Promise.all(goalList.map((goal) => fetchTasksByGoal(goal.id)))).flat();
        setGoals(goalList);
        setTasks(taskList);
      } catch (caughtError) {
        setGoals([]);
        setTasks([]);
        setError(caughtError.message || "Failed to load stats.");
      } finally {
        setLoading(false);
      }
    }

    loadStats();
  }, []);

  const todayKey = useMemo(() => dateKey(new Date()), []);
  const completedTasks = useMemo(() => tasks.filter((task) => task.completed).length, [tasks]);
  const pendingTasks = tasks.length - completedTasks;
  const overdueTasks = useMemo(
    () => tasks.filter((task) => !task.completed && task.finishDate && task.finishDate < todayKey).length,
    [tasks, todayKey],
  );
  const dueTodayTasks = useMemo(
    () => tasks.filter((task) => !task.completed && task.finishDate === todayKey).length,
    [tasks, todayKey],
  );

  const completionRate = useMemo(() => {
    if (tasks.length === 0) return 0;
    return Math.round((completedTasks / tasks.length) * 100);
  }, [tasks, completedTasks]);

  const goalBreakdown = useMemo(() => {
    return goals
      .map((goal) => {
        const goalTasks = tasks.filter((task) => task.goalId === goal.id);
        const goalCompleted = goalTasks.filter((task) => task.completed).length;
        const rate = goalTasks.length === 0 ? 0 : Math.round((goalCompleted / goalTasks.length) * 100);
        return {
          id: goal.id,
          title: goal.goal,
          total: goalTasks.length,
          completed: goalCompleted,
          rate,
        };
      })
      .sort((a, b) => {
        if (b.rate !== a.rate) return b.rate - a.rate;
        return b.total - a.total;
      });
  }, [goals, tasks]);

  const recentDays = useMemo(() => {
    const today = new Date();
    const start = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    const days = [];

    for (let offset = 13; offset >= 0; offset -= 1) {
      const day = new Date(start.getTime() - offset * DAY_MS);
      const key = dateKey(day);
      const dayTasks = tasks.filter((task) => task.finishDate === key);
      const done = dayTasks.filter((task) => task.completed).length;
      days.push({
        key,
        label: day.toLocaleDateString(undefined, { weekday: "short" }),
        shortDate: day.toLocaleDateString(undefined, { month: "short", day: "numeric" }),
        total: dayTasks.length,
        done,
      });
    }

    return days;
  }, [tasks]);

  const bestDay = useMemo(() => {
    return recentDays.reduce(
      (best, day) => {
        if (day.done > best.done) return day;
        return best;
      },
      { done: 0, shortDate: "N/A" },
    );
  }, [recentDays]);

  const weeklyMomentum = useMemo(() => {
    const lastWeek = recentDays.slice(-7);
    const totalDone = lastWeek.reduce((sum, day) => sum + day.done, 0);
    const totalPlanned = lastWeek.reduce((sum, day) => sum + day.total, 0);
    const percent = totalPlanned === 0 ? 0 : Math.round((totalDone / totalPlanned) * 100);
    return { totalDone, totalPlanned, percent };
  }, [recentDays]);

  const historyItems = useMemo(() => {
    return [...tasks]
      .filter((task) => task.finishDate)
      .sort((a, b) => {
        if (a.finishDate === b.finishDate) {
          return Number(b.completed) - Number(a.completed);
        }
        return a.finishDate < b.finishDate ? 1 : -1;
      })
      .slice(0, 12)
      .map((task) => {
        const goalTitle = goals.find((goal) => goal.id === task.goalId)?.goal || "Unknown goal";
        return {
          id: task.id,
          title: task.task,
          finishDate: task.finishDate,
          completed: task.completed,
          goalTitle,
        };
      });
  }, [goals, tasks]);

  const maxDayDone = useMemo(() => {
    const values = recentDays.map((day) => day.done);
    return Math.max(1, ...values);
  }, [recentDays]);

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Stats / History</h1>
          <p>Cold facts. No excuses.</p>
        </div>
      </header>

      {error ? <div className="inline-form-error">{error}</div> : null}

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
        <div className="stat-card">
          <div className="stat-label">Pending</div>
          <div className="stat-value">{pendingTasks}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Due today</div>
          <div className="stat-value">{dueTodayTasks}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Overdue</div>
          <div className="stat-value">{overdueTasks}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Best day (14d)</div>
          <div className="stat-value">{bestDay.done}</div>
          <div className="stat-footnote">{bestDay.shortDate}</div>
        </div>
      </section>

      <section className="stats-panels">
        <div className="stats-panel">
          <div className="section-title">Streak history (14 days)</div>
          {loading ? (
            <div className="empty-state">Loading...</div>
          ) : recentDays.length === 0 ? (
            <div className="empty-state">No task data yet.</div>
          ) : (
            <>
              <div className="streak-grid">
                {recentDays.map((day) => (
                  <div key={day.key} className="streak-day">
                    <div
                      className="streak-bar"
                      style={{
                        height: `${Math.max(8, Math.round((day.done / maxDayDone) * 100))}%`,
                        opacity: day.done === 0 ? 0.26 : 1,
                      }}
                      title={`${day.shortDate}: ${day.done}/${day.total} completed`}
                    />
                    <div className="streak-day-label">{day.label}</div>
                  </div>
                ))}
              </div>
              <div className="stats-footnote">
                Last 7 days: {weeklyMomentum.totalDone}/{weeklyMomentum.totalPlanned} completed ({weeklyMomentum.percent}%).
              </div>
            </>
          )}
        </div>
        <div className="stats-panel">
          <div className="section-title">Completion rates by goal</div>
          {loading ? (
            <div className="empty-state">Loading...</div>
          ) : goalBreakdown.length === 0 ? (
            <div className="empty-state">Create goals to unlock rate tracking.</div>
          ) : (
            <div className="goal-rates">
              {goalBreakdown.map((goal) => (
                <div key={goal.id} className="goal-rate-row">
                  <div className="goal-rate-head">
                    <div className="goal-rate-title">{goal.title}</div>
                    <div className="goal-rate-meta">
                      {goal.completed}/{goal.total} ({goal.rate}%)
                    </div>
                  </div>
                  <div className="progress">
                    <div className="progress-bar" style={{ width: `${goal.rate}%` }} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </section>

      <section className="stats-panel">
        <div className="section-title">Recent history</div>
        {loading ? (
          <div className="empty-state">Loading...</div>
        ) : historyItems.length === 0 ? (
          <div className="empty-state">No history yet. Add tasks with dates to see your timeline.</div>
        ) : (
          <div className="history-list">
            {historyItems.map((item) => (
              <div key={item.id} className="history-row">
                <div>
                  <div className="history-title">{item.title}</div>
                  <div className="history-goal">{item.goalTitle}</div>
                </div>
                <div className="history-right">
                  <div className={`history-status ${item.completed ? "is-done" : "is-pending"}`}>
                    {item.completed ? "Completed" : "Pending"}
                  </div>
                  <div className="history-date">{formatDate(item.finishDate)}</div>
                  <div className="history-age">{distanceLabel(item.finishDate)}</div>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
