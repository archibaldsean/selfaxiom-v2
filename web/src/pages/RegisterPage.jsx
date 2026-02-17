import { useState } from "react";
import { Link } from "react-router-dom";

export default function RegisterPage() {
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
      const response = await fetch("/api/v1/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, username, password }),
      });

      if (response.ok) {
        setSuccess("Account created successfully!");
        setEmail("");
        setUsername("");
        setPassword("");
        return;
      }
      if (response.status === 409) {
        setError(data.message || "Email or username already exists.");
      }
      else if (response.status === 400) {
        // backend validation error
        const fieldErrors = data.fieldErrors ? Object.values(data.fieldErrors).join(" ") : "";
        setError(fieldErrors || data.message || "An error occurred. Please try again.");
      }
      else {
        setError("An error occurred. Please try again.");
      }

    } catch (error) {
      setError("An error occurred. Please try again.");
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
