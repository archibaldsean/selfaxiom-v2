const AUTH_USER_KEY = "selfaxiom.auth.user";
const AUTH_ACCESS_TOKEN_KEY = "selfaxiom.auth.accessToken";

let accessTokenCache = null;

export function getAuthUser() {
  if (typeof window === "undefined") return null;
  const raw = window.localStorage.getItem(AUTH_USER_KEY);
  if (!raw) return null;

  try {
    const parsed = JSON.parse(raw);
    if (!parsed || typeof parsed !== "object") return null;
    if (!parsed.id) return null;
    return parsed;
  } catch {
    return null;
  }
}

export function getAccessToken() {
  if (accessTokenCache) return accessTokenCache;
  if (typeof window === "undefined") return null;

  const persisted = window.sessionStorage.getItem(AUTH_ACCESS_TOKEN_KEY);
  if (!persisted) return null;
  accessTokenCache = persisted;
  return accessTokenCache;
}

export function setAuthSession(payload) {
  if (typeof window === "undefined") return;

  const user = payload?.user || null;
  const nextAccessToken = payload?.accessToken || null;

  if (user?.id) {
    window.localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user));
  } else {
    window.localStorage.removeItem(AUTH_USER_KEY);
  }

  if (nextAccessToken) {
    accessTokenCache = nextAccessToken;
    window.sessionStorage.setItem(AUTH_ACCESS_TOKEN_KEY, nextAccessToken);
  } else {
    accessTokenCache = null;
    window.sessionStorage.removeItem(AUTH_ACCESS_TOKEN_KEY);
  }
}

export function clearAuthSession() {
  if (typeof window === "undefined") return;
  accessTokenCache = null;
  window.localStorage.removeItem(AUTH_USER_KEY);
  window.sessionStorage.removeItem(AUTH_ACCESS_TOKEN_KEY);
}

export async function login(identifier, password) {
  const response = await fetch("/api/v1/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ identifier, password }),
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw buildAuthError(response.status, data);
  }

  setAuthSession(data);
  return data;
}

export async function register({ username, email, password }) {
  const response = await fetch("/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ username, email, password }),
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw buildAuthError(response.status, data);
  }

  setAuthSession(data);
  return data;
}

export async function refreshSession() {
  const response = await fetch("/api/v1/auth/refresh", {
    method: "POST",
    credentials: "include",
  });

  if (!response.ok) {
    clearAuthSession();
    return null;
  }

  const data = await response.json().catch(() => null);
  if (!data?.user?.id || !data?.accessToken) {
    clearAuthSession();
    return null;
  }

  setAuthSession(data);
  return data;
}

export async function logout() {
  try {
    await fetch("/api/v1/auth/logout", {
      method: "POST",
      credentials: "include",
    });
  } finally {
    clearAuthSession();
  }
}

export async function authFetch(path, options = {}, retry = true) {
  const headers = new Headers(options.headers || {});
  const token = getAccessToken();
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(path, {
    ...options,
    headers,
    credentials: "include",
  });

  if (response.status !== 401 || !retry) {
    return response;
  }

  const refreshed = await refreshSession();
  if (!refreshed?.accessToken) {
    return response;
  }

  const retryHeaders = new Headers(options.headers || {});
  retryHeaders.set("Authorization", `Bearer ${refreshed.accessToken}`);
  return fetch(path, {
    ...options,
    headers: retryHeaders,
    credentials: "include",
  });
}

function buildAuthError(status, payload) {
  const error = new Error(payload?.message || "Authentication failed");
  error.status = status;
  error.fieldErrors = payload?.fieldErrors || null;
  return error;
}
