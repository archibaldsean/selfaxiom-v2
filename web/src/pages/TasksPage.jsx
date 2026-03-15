import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import TaskForm from "../components/ui/TaskForm";
import TaskItem from "../components/ui/TaskItem";
import { createTask, fetchGoals, fetchTasksByGoal } from "../lib/api";
import { getAuthUser } from "../lib/auth";

export default function TasksPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [goals, setGoals] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const selectedGoalId = Number(searchParams.get("goalId") || "0");
  const selectedDay = searchParams.get("day") || new Date().toISOString().slice(0, 10);

  async function loadData(goalIdFromQuery) {
    setLoading(true);
    setError("");

    try {
      const user = getAuthUser();
      const goalList = await fetchGoals(user?.id);
      setGoals(goalList);

      const fallbackGoalId = goalList[0]?.id || 0;
      const goalId = goalIdFromQuery || fallbackGoalId;
      const taskList = goalId ? await fetchTasksByGoal(goalId) : [];
      setTasks(taskList);

      if (!goalIdFromQuery && fallbackGoalId) {
        setSearchParams({ goalId: String(fallbackGoalId), day: selectedDay });
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

  const filteredTasks = useMemo(() => {
    return tasks.filter((task) => {
      if (!task.finishDate) return false;
      return task.finishDate === selectedDay;
    });
  }, [tasks, selectedDay]);

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
              setSearchParams({ goalId: nextGoalId, day: selectedDay });
            }}
          >
            {goals.length === 0 ? <option value="">No goals</option> : null}
            {goals.map((goal) => (
              <option key={goal.id} value={goal.id}>
                {goal.goal}
              </option>
            ))}
          </select>
        </div>
      </div>

      {error ? <div className="inline-form-error">{error}</div> : null}

      <section className="task-list">
        {loading ? <div className="empty-state">Loading...</div> : null}
        {!loading && filteredTasks.length === 0 ? (
          <div className="empty-state">No tasks scheduled for this day.</div>
        ) : (
          <div className="task-list-rows">
            {filteredTasks.map((task) => (
              <div className="task-row-wrap" key={task.id}>
                <TaskItem
                  title={task.task}
                  completed={task.completed}
                  finishDate={task.finishDate}
                  goalTitle={goals.find((goal) => goal.id === task.goalId)?.goal || ""}
                />
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
