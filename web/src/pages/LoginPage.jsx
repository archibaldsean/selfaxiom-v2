import { Link } from "react-router-dom";

export default function LoginPage() {
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

          <form className="auth-form" onSubmit={(event) => event.preventDefault()}>
            <label className="auth-label" htmlFor="identifier">
              Email or Username
            </label>
            <input id="identifier" name="identifier" required className="auth-input" />

            <label className="auth-label" htmlFor="password">
              Password
            </label>
            <input id="password" name="password" type="password" required className="auth-input" />

            <button type="submit" className="auth-button">
              Login
            </button>
          </form>

          <div className="auth-alt">
            No account yet? <Link to="/register">Create one</Link>
          </div>
        </section>
      </main>
    </div>
  );
}
