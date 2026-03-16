import { getAuthUser, logout } from "../lib/auth";

export default function SettingsPage() {
  const user = getAuthUser();

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Settings</h1>
          <p>Keep it boring. Keep it reliable.</p>
        </div>
      </header>

      <section className="settings-grid">
        <div className="settings-card">
          <div className="section-title">Profile</div>
          <div className="settings-row">
            <div>
              <div className="settings-label">Email</div>
              <div className="settings-value">{user?.email || "Unknown"}</div>
            </div>
            <button className="ghost-button" type="button">
              Edit
            </button>
          </div>
          <div className="settings-row">
            <div>
              <div className="settings-label">Username</div>
              <div className="settings-value">{user?.username || "Unknown"}</div>
            </div>
            <button className="ghost-button" type="button">
              Edit
            </button>
          </div>
        </div>

        <div className="settings-card">
          <div className="section-title">Preferences</div>
          <div className="settings-row">
            <div>
              <div className="settings-label">Notifications</div>
              <div className="settings-value">Daily summary</div>
            </div>
            <button className="ghost-button" type="button">
              Adjust
            </button>
          </div>
          <div className="settings-row">
            <div>
              <div className="settings-label">Theme</div>
              <div className="settings-value">Dark</div>
            </div>
            <button className="ghost-button" type="button">
              Toggle
            </button>
          </div>
        </div>

        <div className="settings-card">
          <div className="section-title">Data</div>
          <div className="settings-row">
            <div>
              <div className="settings-label">Export</div>
              <div className="settings-value">CSV / JSON</div>
            </div>
            <button className="ghost-button" type="button">
              Export
            </button>
          </div>
          <div className="settings-row">
            <div>
              <div className="settings-label">Logout</div>
              <div className="settings-value">End session</div>
            </div>
            <button
              className="ghost-button"
              type="button"
              onClick={async () => {
                await logout();
                window.location.href = "/";
              }}
            >
              Logout
            </button>
          </div>
        </div>
      </section>
    </div>
  );
}
