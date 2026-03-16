import { useEffect, useState } from "react";
import { Navigate, Outlet, Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import DashboardPage from "./pages/DashboardPage";
import GoalsPage from "./pages/GoalsPage";
import TasksPage from "./pages/TasksPage";
import SchedulePage from "./pages/SchedulePage";
import StatsPage from "./pages/StatsPage";
import RewardsPage from "./pages/RewardsPage";
import SettingsPage from "./pages/SettingsPage";
import AppSidebar from "./components/ui/AppSidebar";
import AppTopbar from "./components/ui/AppTopbar";
import { getAuthUser, refreshSession } from "./lib/auth";

function RequireAuth() {
  const [ready, setReady] = useState(false);
  const [user, setUser] = useState(getAuthUser());

  useEffect(() => {
    let cancelled = false;

    async function bootstrapSession() {
      if (!user?.id) {
        await refreshSession();
      }

      if (!cancelled) {
        setUser(getAuthUser());
        setReady(true);
      }
    }

    bootstrapSession();
    return () => {
      cancelled = true;
    };
  }, []);

  if (!ready) {
    return <div className="empty-state">Loading session...</div>;
  }

  if (!user?.id) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

function DashboardLayout() {
  return (
    <div className="app-shell">
      <AppTopbar />
      <div className="app-body">
        <AppSidebar />
        <main className="app-main">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route element={<RequireAuth />}>
        <Route element={<DashboardLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/goals" element={<GoalsPage />} />
          <Route path="/tasks" element={<TasksPage />} />
          <Route path="/schedule" element={<SchedulePage />} />
          <Route path="/stats" element={<StatsPage />} />
          <Route path="/rewards" element={<RewardsPage />} />
          <Route path="/settings" element={<SettingsPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
