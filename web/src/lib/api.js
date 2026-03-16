import { authFetch } from "./auth";

async function requestJson(path, options = {}) {
  const response = await authFetch(path, options);
  const payload = await response.json().catch(() => null);

  if (response.status === 204) {
    return null;
  }

  if (!response.ok) {
    const message = payload?.message || "Request failed.";
    throw new Error(message);
  }

  return payload;
}

export async function fetchGoals() {
  return requestJson("/api/v1/goals");
}

export async function createGoal({ goal, description, finishDate }) {
  return requestJson("/api/v1/goals", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ goal, description, finishDate }),
  });
}

export async function fetchGoalById(goalId) {
  if (!goalId) return null;
  return requestJson(`/api/v1/goals/${goalId}`);
}

export async function updateGoal({ goalId, goal, description, finishDate }) {
  return requestJson(`/api/v1/goals/${goalId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ goal, description, finishDate }),
  });
}

export async function deleteGoal(goalId) {
  if (!goalId) return;
  return requestJson(`/api/v1/goals/${goalId}`, {
    method: "DELETE",
  });
}

export async function fetchTasksByGoal(goalId) {
  if (!goalId) return [];
  return requestJson(`/api/v1/goals/${goalId}/tasks`);
}

export async function createTask({ goalId, task, description, finishDate }) {
  return requestJson(`/api/v1/goals/${goalId}/tasks`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ task, description, finishDate }),
  });
}

export async function fetchTaskById(goalId, taskId) {
  if (!goalId || !taskId) return null;
  return requestJson(`/api/v1/goals/${goalId}/tasks/${taskId}`);
}

export async function updateTask({ goalId, taskId, task, description, finishDate, completed }) {
  return requestJson(`/api/v1/goals/${goalId}/tasks/${taskId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ task, description, finishDate, completed }),
  });
}

export async function deleteTask(goalId, taskId) {
  if (!goalId || !taskId) return;
  return requestJson(`/api/v1/goals/${goalId}/tasks/${taskId}`, {
    method: "DELETE",
  });
}

export async function fetchRewardSummary() {
  return requestJson("/api/v1/rewards/summary");
}

export async function fetchRewardHistory() {
  return requestJson("/api/v1/rewards/history");
}
