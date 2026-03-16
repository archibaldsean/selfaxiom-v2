import { useEffect, useMemo, useState } from "react";
import { fetchRewardHistory, fetchRewardSummary } from "../lib/api";

function formatEvent(eventType) {
  if (eventType === "TASK_COMPLETED") return "Task completed";
  if (eventType === "GOAL_COMPLETED") return "Goal completed";
  return eventType;
}

function formatRelative(dateString) {
  if (!dateString) return "Just now";
  const now = Date.now();
  const value = new Date(dateString).getTime();
  if (Number.isNaN(value)) return "Unknown";
  const minutes = Math.floor((now - value) / (60 * 1000));
  if (minutes < 1) return "Just now";
  if (minutes < 60) return `${minutes}m ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  return `${days}d ago`;
}

export default function RewardsPage() {
  const [summary, setSummary] = useState({ pointsBalance: 0, totalEarned: 0 });
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadRewards() {
      setLoading(true);
      setError("");

      try {
        const [nextSummary, nextHistory] = await Promise.all([fetchRewardSummary(), fetchRewardHistory()]);
        setSummary(nextSummary || { pointsBalance: 0, totalEarned: 0 });
        setHistory(Array.isArray(nextHistory) ? nextHistory : []);
      } catch (caughtError) {
        setSummary({ pointsBalance: 0, totalEarned: 0 });
        setHistory([]);
        setError(caughtError.message || "Failed to load rewards.");
      } finally {
        setLoading(false);
      }
    }

    loadRewards();
  }, []);

  const latestEventAge = useMemo(() => {
    if (history.length === 0) return "No reward events yet";
    return formatRelative(history[0].createdAt);
  }, [history]);

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Rewards</h1>
          <p>Earned, not given.</p>
        </div>
      </header>

      {error ? <div className="inline-form-error">{error}</div> : null}

      <section className="rewards-balance">
        <div className="rewards-balance-card">
          <div className="stat-label">Points balance</div>
          <div className="stat-value">{loading ? "..." : summary.pointsBalance}</div>
        </div>
        <div className="rewards-balance-card">
          <div className="stat-label">Last reward event</div>
          <div className="stat-value">{loading ? "..." : latestEventAge}</div>
        </div>
        <div className="rewards-balance-card">
          <div className="stat-label">Lifetime earned</div>
          <div className="stat-value">{loading ? "..." : summary.totalEarned}</div>
        </div>
      </section>

      <section className="rewards-grid">
        <div className="reward-card">
          <h3>Task completion reward</h3>
          <div className="reward-meta">+10 points when task is marked complete</div>
        </div>
        <div className="reward-card">
          <h3>Goal completion reward</h3>
          <div className="reward-meta">+100 points when all goal tasks are complete</div>
        </div>
        <div className="reward-card">
          <h3>Fair rewards</h3>
          <div className="reward-meta">Each task and goal can only earn points once</div>
        </div>
      </section>

      <section className="rewards-history">
        <div className="section-title">Rewards history</div>
        {loading ? (
          <div className="empty-state">Loading...</div>
        ) : history.length === 0 ? (
          <div className="empty-state">No reward events yet. Complete a task to earn points.</div>
        ) : (
          <div className="history-list">
            {history.map((item) => (
              <div key={item.id} className="history-row">
                <div>
                  <div className="history-title">{formatEvent(item.eventType)}</div>
                  <div className="history-goal">{formatRelative(item.createdAt)}</div>
                </div>
                <div className="history-right">
                  <div className={`history-status ${item.pointsDelta >= 0 ? "is-done" : "is-pending"}`}>
                    {item.pointsDelta >= 0 ? `+${item.pointsDelta}` : item.pointsDelta}
                  </div>
                  <div className="history-date">Balance: {item.balanceAfter}</div>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
