import { useState } from "react";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { login } from "../lib/auth";

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
      const data = await login(identifier, password);
      setSuccess(`Welcome back, ${data.user?.username || "user"}!`);
      setIdentifier("");
      setPassword("");
      navigate("/dashboard", { replace: true });
    } catch (caughtError) {
      if (caughtError?.status === 401) {
        setError(caughtError.message || "Invalid credentials.");
      } else if (caughtError?.status === 400) {
        const fieldErrors = caughtError.fieldErrors ? Object.values(caughtError.fieldErrors).join(" ") : "";
        setError(fieldErrors || caughtError.message || "Please check your input.");
      } else {
        setError(caughtError.message || "Network error. Please try again.");
      }
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
