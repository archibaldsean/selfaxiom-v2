import { useEffect, useMemo, useState } from "react";
import GoalForm from "../components/ui/GoalForm";
import GoalItem from "../components/ui/GoalItem";
import { createGoal, fetchGoals, fetchTasksByGoal } from "../lib/api";
import { getAuthUser } from "../lib/auth";

export default function GoalsPage() {
  const [goals, setGoals] = useState([]);
  const [tasksByGoal, setTasksByGoal] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadGoals() {
    setLoading(true);
    setError("");

    try {
      const user = getAuthUser();
      const goalList = await fetchGoals(user?.id);
      const taskResults = await Promise.all(goalList.map((goal) => fetchTasksByGoal(goal.id)));

      const nextTasksByGoal = {};
      goalList.forEach((goal, index) => {
        nextTasksByGoal[goal.id] = taskResults[index] || [];
      });

      setGoals(goalList);
      setTasksByGoal(nextTasksByGoal);
    } catch (caughtError) {
      setError(caughtError.message || "Failed to load goals.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadGoals();
  }, []);

  async function handleCreateGoal(values) {
    const user = getAuthUser();
    await createGoal({
      userId: user?.id,
      goal: values.goal,
      finishDate: values.finishDate,
    });
    await loadGoals();
  }

  const featuredGoal = useMemo(() => goals[0], [goals]);

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Goals</h1>
          <p>Define the obsession and keep it visible.</p>
        </div>
      </header>

      <div className="page-actions">
        <GoalForm onCreate={handleCreateGoal} defaultDate={new Date().toISOString().slice(0, 10)} />
      </div>

      {error ? <div className="inline-form-error">{error}</div> : null}

      <section className="goals-grid">
        {loading ? <div className="empty-state">Loading...</div> : null}
        {!loading && goals.length === 0 ? <div className="empty-state">No goals yet. Create your first obsession.</div> : null}
        {goals.map((goal) => {
          const tasks = tasksByGoal[goal.id] || [];
          const completedTaskCount = tasks.filter((task) => task.completed).length;

          return (
            <GoalItem
              key={goal.id}
              title={goal.goal}
              completed={goal.completed}
              finishDate={goal.finishDate}
              taskCount={tasks.length}
              completedTaskCount={completedTaskCount}
            />
          );
        })}
      </section>

      <section className="goal-detail">
        <div className="section-title">Goal Detail</div>
        {featuredGoal ? (
          <div className="goal-detail-grid">
            <div>
              <h3>{featuredGoal.goal}</h3>
              <p>Keep this goal visible and ship tasks daily.</p>
              <div className="goal-detail-block">
                <div className="goal-detail-label">Status</div>
                <div>{featuredGoal.completed ? "done" : "active"}</div>
              </div>
            </div>
            <div>
              <div className="goal-detail-block">
                <div className="goal-detail-label">Tasks</div>
                <div>{(tasksByGoal[featuredGoal.id] || []).length}</div>
              </div>
              <div className="goal-detail-block">
                <div className="goal-detail-label">Target date</div>
                <div>{featuredGoal.finishDate || "Not set"}</div>
              </div>
            </div>
          </div>
        ) : (
          <div className="empty-state">Pick a goal to see the detail.</div>
        )}
      </section>
    </div>
  );
}
