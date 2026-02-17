import { Link } from "react-router-dom";

export default function RegisterPage() {
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

          <form className="auth-form" onSubmit={(event) => event.preventDefault()}>
            <label className="auth-label" htmlFor="email">
              Email
            </label>
            <input id="email" name="email" type="email" required className="auth-input" />

            <label className="auth-label" htmlFor="username">
              Username
            </label>
            <input id="username" name="username" required className="auth-input" />

            <label className="auth-label" htmlFor="password">
              Password
            </label>
            <input id="password" name="password" type="password" required className="auth-input" />

            <button type="submit" className="auth-button">
              Create account
            </button>
          </form>

          <div className="auth-alt">
            Already have an account? <Link to="/login">Sign in</Link>
          </div>
        </section>
      </main>
    </div>
  );
}
