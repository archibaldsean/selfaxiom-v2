import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import ConfirmModal from "../components/ui/ConfirmModal";
import TaskEditModal from "../components/ui/TaskEditModal";
import TaskForm from "../components/ui/TaskForm";
import TaskItem from "../components/ui/TaskItem";
import { createTask, deleteTask, fetchGoals, fetchTasksByGoal, updateTask } from "../lib/api";

export default function TasksPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [goals, setGoals] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [editingTask, setEditingTask] = useState(null);
  const [deletingTask, setDeletingTask] = useState(null);
  const [busyDelete, setBusyDelete] = useState(false);

  const selectedGoalId = Number(searchParams.get("goalId") || "0");
  const selectedDay = searchParams.get("day") || new Date().toISOString().slice(0, 10);
  const viewMode = searchParams.get("view") === "all" ? "all" : "day";

  async function loadData(goalIdFromQuery) {
    setLoading(true);
    setError("");

    try {
      const goalList = await fetchGoals();
      setGoals(goalList);

      const fallbackGoalId = goalList[0]?.id || 0;
      const goalId = goalIdFromQuery || fallbackGoalId;
      const taskList = goalId ? await fetchTasksByGoal(goalId) : [];
      setTasks(taskList);

      if (!goalIdFromQuery && fallbackGoalId) {
        setSearchParams({ goalId: String(fallbackGoalId), day: selectedDay, view: viewMode });
      }
    } catch (caughtError) {
      setError(caughtError.message || "Failed to load tasks.");
      setTasks([]);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData(selectedGoalId);
  }, [selectedGoalId]);

  async function handleCreateTask(values) {
    await createTask(values);
    await loadData(values.goalId);
  }

  async function handleToggleTask(task) {
    try {
      await updateTask({
        goalId: task.goalId,
        taskId: task.id,
        task: task.task,
        description: task.description,
        finishDate: task.finishDate,
        completed: !task.completed,
      });
      await loadData(task.goalId);
    } catch (caughtError) {
      setError(caughtError.message || "Failed to update task.");
    }
  }

  async function handleSaveTask(values) {
    if (!editingTask) return;

    await updateTask({
      goalId: editingTask.goalId,
      taskId: editingTask.id,
      task: values.task,
      description: values.description,
      finishDate: values.finishDate,
      completed: editingTask.completed,
    });

    setEditingTask(null);
    await loadData(editingTask.goalId);
  }

  async function handleDeleteTask() {
    if (!deletingTask) return;
    setBusyDelete(true);

    try {
      await deleteTask(deletingTask.goalId, deletingTask.id);
      setDeletingTask(null);
      await loadData(selectedGoalId);
    } catch (caughtError) {
      setError(caughtError.message || "Failed to delete task.");
    } finally {
      setBusyDelete(false);
    }
  }

  const visibleTasks = useMemo(() => {
    if (viewMode === "all") return tasks;

    return tasks.filter((task) => {
      if (!task.finishDate) return false;
      return task.finishDate === selectedDay;
    });
  }, [tasks, selectedDay, viewMode]);

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Tasks</h1>
          <p>Control and clarity. Edit tasks here.</p>
        </div>
      </header>

      <div className="page-actions">
        <TaskForm
          goals={goals.map((goal) => ({ id: goal.id, title: goal.goal }))}
          defaultGoalId={selectedGoalId ? String(selectedGoalId) : ""}
          defaultDate={selectedDay}
          onCreate={handleCreateTask}
        />
      </div>

      <div className="task-toolbar">
        <form
          className="task-date-form"
          onSubmit={(event) => {
            event.preventDefault();
            const form = new FormData(event.currentTarget);
            const nextDay = String(form.get("day") || selectedDay);
            setSearchParams({
              goalId: String(selectedGoalId || goals[0]?.id || ""),
              day: nextDay,
              view: "day",
            });
          }}
        >
          <label className="inline-form-label" htmlFor="task-day">
            Day
          </label>
          <input id="task-day" name="day" type="date" className="inline-form-input" defaultValue={selectedDay} />
          <button className="ghost-button" type="submit">
            Load
          </button>
        </form>

        <div className="task-view-toggle">
          <select
            className="inline-form-input"
            value={selectedGoalId || ""}
            onChange={(event) => {
              const nextGoalId = event.target.value;
              setSearchParams({ goalId: nextGoalId, day: selectedDay, view: viewMode });
            }}
          >
            {goals.length === 0 ? <option value="">No goals</option> : null}
            {goals.map((goal) => (
              <option key={goal.id} value={goal.id}>
                {goal.goal}
              </option>
            ))}
          </select>
          <button
            className="ghost-button"
            type="button"
            onClick={() =>
              setSearchParams({
                goalId: String(selectedGoalId || goals[0]?.id || ""),
                day: selectedDay,
                view: viewMode === "all" ? "day" : "all",
              })
            }
          >
            {viewMode === "all" ? "View by day" : "View all for goal"}
          </button>
        </div>
      </div>

      {error ? <div className="inline-form-error">{error}</div> : null}

      <section className="task-list">
        {loading ? <div className="empty-state">Loading...</div> : null}
        {!loading && visibleTasks.length === 0 ? (
          <div className="empty-state">
            {viewMode === "all" ? "No tasks found for this goal." : "No tasks scheduled for this day."}
          </div>
        ) : (
          <div className="task-list-rows">
            {visibleTasks.map((task) => (
              <div className="task-row-wrap" key={task.id}>
                <TaskItem
                  title={task.task}
                  description={task.description}
                  completed={task.completed}
                  finishDate={task.finishDate}
                  goalTitle={goals.find((goal) => goal.id === task.goalId)?.goal || ""}
                  onToggleComplete={() => handleToggleTask(task)}
                  onEdit={() => setEditingTask(task)}
                  onDelete={() => setDeletingTask(task)}
                />
              </div>
            ))}
          </div>
        )}
      </section>

      {editingTask ? (
        <TaskEditModal
          task={editingTask}
          onClose={() => setEditingTask(null)}
          onSave={handleSaveTask}
        />
      ) : null}

      {deletingTask ? (
        <ConfirmModal
          title="Delete task"
          message={`Delete \"${deletingTask.task}\"?`}
          confirmLabel="Delete task"
          busy={busyDelete}
          onClose={() => setDeletingTask(null)}
          onConfirm={handleDeleteTask}
        />
      ) : null}
    </div>
  );
}
