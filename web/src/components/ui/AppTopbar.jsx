import { useNavigate } from "react-router-dom";
import { clearAuthUser, getAuthUser } from "../../lib/auth";

export default function AppTopbar() {
  const navigate = useNavigate();
  const user = getAuthUser();

  return (
    <header className="app-topbar">
      <div className="app-topbar-left">
        <div className="app-mark">SA</div>
        <div className="app-title">SelfAxiom</div>
      </div>
      <div className="app-topbar-center">
        <div className="app-status">Operator: {user?.username || "Unknown"}</div>
        <div className="app-status">Email: {user?.email || "N/A"}</div>
      </div>
      <div className="app-topbar-right">
        <button
          className="app-logout"
          type="button"
          onClick={() => {
            clearAuthUser();
            navigate("/", { replace: true });
          }}
        >
          Logout
        </button>
      </div>
    </header>
  );
}
