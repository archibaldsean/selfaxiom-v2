import { useState } from "react";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { register } from "../lib/auth";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
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
      await register({ email, username, password });
      setSuccess("Account created successfully!");
      setEmail("");
      setUsername("");
      setPassword("");
      navigate("/dashboard", { replace: true });
    } catch (caughtError) {
      if (caughtError?.status === 409) {
        setError(caughtError.message || "Email or username already exists.");
      } else if (caughtError?.status === 400) {
        const fieldErrors = caughtError.fieldErrors ? Object.values(caughtError.fieldErrors).join(" ") : "";
        setError(fieldErrors || caughtError.message || "An error occurred. Please try again.");
      } else {
        setError(caughtError.message || "An error occurred. Please try again.");
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
          <Link to="/login">Login</Link>
        </nav>
      </header>

      <main className="auth-main">
        <section className="auth-card">
          <div className="auth-title">Create your system</div>
          <p className="auth-subtitle">Start the loop and protect the streak.</p>

          <form className="auth-form" onSubmit={handleSubmit}>
            <label className="auth-label" htmlFor="email">
              Email
            </label>
            <input
              id="email"
              name="email"
              type="email"
              required className="auth-input"
              value={email}
              onChange={(event) => setEmail(event.target.value)} />
            <label className="auth-label" htmlFor="username">
              Username
            </label>
            <input
              id="username"
              name="username"
              required className="auth-input"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
            />

            <label className="auth-label" htmlFor="password">
              Password
            </label>
            <input
              id="password"
              name="password"
              type="password"
              required className="auth-input"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />

            <button type="submit" className="auth-button" disabled={loading}>
              {loading ? "Creating..." : "Create Account"}
            </button>

            {error && <div className="auth-error">{error}</div>}
            {success && <div className="auth-success">{success}</div>}
          </form>

          <div className="auth-alt">
            Already have an account? <Link to="/login">Sign in</Link>
          </div>
        </section>
      </main>
    </div>
  );
}
