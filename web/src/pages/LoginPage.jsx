import { useState } from "react";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { setAuthUser } from "../lib/auth";

export default function LoginPage() {
  const navigate = useNavigate();
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setSuccess("");
    setLoading(true);

    try {
      const response = await fetch("/api/v1/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ identifier, password }),
      });

      const data = await response.json().catch(() => ({}));

      if (response.ok) {
        setAuthUser(data);
        setSuccess(`Welcome back, ${data.username || "user"}!`);
        setIdentifier("");
        setPassword("");
        navigate("/dashboard", { replace: true });
        return;
      }

      if (response.status === 401) {
        setError(data.message || "Invalid credentials.");
      } else if (response.status === 400) {
        const fieldErrors = data.fieldErrors ? Object.values(data.fieldErrors).join(" ") : "";
        setError(fieldErrors || data.message || "Please check your input.");
      } else {
        setError(data.message || "An error occurred. Please try again.");
      }
    } catch (caughtError) {
      setError("Network error. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-shell">
      <header className="auth-header">
        <div className="logo">SelfAxiom</div>
        <nav className="auth-links">
          <Link to="/register" className="primary">
            Register
          </Link>
        </nav>
      </header>

      <main className="auth-main">
        <section className="auth-card">
          <div className="auth-title">Welcome back</div>
          <p className="auth-subtitle">Lock in. Keep the streak alive.</p>

          <form className="auth-form" onSubmit={handleSubmit}>
            <label className="auth-label" htmlFor="identifier">
              Email or Username
            </label>
            <input
              id="identifier"
              name="identifier"
              required
              className="auth-input"
              value={identifier}
              onChange={(event) => setIdentifier(event.target.value)}
            />

            <label className="auth-label" htmlFor="password">
              Password
            </label>
            <input
              id="password"
              name="password"
              type="password"
              required
              className="auth-input"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />

            <button type="submit" className="auth-button" disabled={loading}>
              {loading ? "Logging in..." : "Login"}
            </button>

            {error && <div className="auth-error">{error}</div>}
            {success && <div className="auth-success">{success}</div>}
          </form>

          <div className="auth-alt">
            No account yet? <Link to="/register">Create one</Link>
          </div>
        </section>
      </main>
    </div>
  );
}
