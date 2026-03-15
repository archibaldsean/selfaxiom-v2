async function requestJson(path, options = {}) {
  const response = await fetch(path, options);
  const payload = await response.json().catch(() => ({}));
  if (!response.ok) {
    const message = payload.message || "Request failed.";
    throw new Error(message);
  }
  return payload;
}

export async function fetchGoals(userId) {
  if (!userId) return [];
  return requestJson(`/api/v1/goals/user/${userId}`);
}

export async function createGoal({ userId, goal, finishDate }) {
  return requestJson("/api/v1/goals", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ userId, goal, finishDate }),
  });
}

export async function fetchTasksByGoal(goalId) {
  if (!goalId) return [];
  return requestJson(`/api/v1/goals/${goalId}/tasks`);
}

export async function createTask({ goalId, task, finishDate }) {
  return requestJson(`/api/v1/goals/${goalId}/tasks`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ task, finishDate }),
  });
}
