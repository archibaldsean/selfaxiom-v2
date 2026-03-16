import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import GoalForm from "../components/ui/GoalForm";
import GoalItem from "../components/ui/GoalItem";
import ConfirmModal from "../components/ui/ConfirmModal";
import GoalEditModal from "../components/ui/GoalEditModal";
import { createGoal, deleteGoal, fetchGoals, fetchTasksByGoal, updateGoal } from "../lib/api";

export default function GoalsPage() {
  const navigate = useNavigate();
  const [goals, setGoals] = useState([]);
  const [tasksByGoal, setTasksByGoal] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [editingGoal, setEditingGoal] = useState(null);
  const [deletingGoal, setDeletingGoal] = useState(null);
  const [busyDelete, setBusyDelete] = useState(false);

  async function loadGoals() {
    setLoading(true);
    setError("");

    try {
      const goalList = await fetchGoals();
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
    await createGoal({
      goal: values.goal,
      description: values.description,
      finishDate: values.finishDate,
    });
    await loadGoals();
  }

  async function handleSaveGoal(values) {
    if (!editingGoal) return;

    await updateGoal({
      goalId: editingGoal.id,
      goal: values.goal,
      description: values.description,
      finishDate: values.finishDate,
    });

    setEditingGoal(null);
    await loadGoals();
  }

  async function handleDeleteGoal() {
    if (!deletingGoal) return;
    setBusyDelete(true);

    try {
      await deleteGoal(deletingGoal.id);
      setDeletingGoal(null);
      await loadGoals();
    } catch (caughtError) {
      setError(caughtError.message || "Failed to delete goal.");
    } finally {
      setBusyDelete(false);
    }
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
              description={goal.description}
              completed={goal.completed}
              finishDate={goal.finishDate}
              taskCount={tasks.length}
              completedTaskCount={completedTaskCount}
              onViewTasks={() => navigate(`/tasks?goalId=${goal.id}&view=all`)}
              onEdit={() => setEditingGoal(goal)}
              onDelete={() => setDeletingGoal(goal)}
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
              <p>{featuredGoal.description || "Add a description to clarify the target and constraints."}</p>
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

      {editingGoal ? (
        <GoalEditModal
          goal={editingGoal}
          onClose={() => setEditingGoal(null)}
          onSave={handleSaveGoal}
        />
      ) : null}

      {deletingGoal ? (
        <ConfirmModal
          title="Delete goal"
          message={`Delete \"${deletingGoal.goal}\"? This also removes every task under it.`}
          confirmLabel="Delete goal"
          busy={busyDelete}
          onClose={() => setDeletingGoal(null)}
          onConfirm={handleDeleteGoal}
        />
      ) : null}
    </div>
  );
}
