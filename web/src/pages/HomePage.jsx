import { Link } from "react-router-dom";

export default function HomePage() {
  return (
    <div className="landing">
      <header className="landing-header">
        <div className="logo">SelfAxiom</div>
        <nav className="auth-links">
          <Link to="/login">Login</Link>
          <Link to="/register" className="primary">
            Register
          </Link>
        </nav>
      </header>

      <section className="hero">
        <h1>Build obsession through clean execution.</h1>
        <p>
          A focused system for goals, tasks, and rewards. Track momentum, protect your streak, and
          make every day intentional.
        </p>
        <div className="hero-actions">
          <Link to="/register" className="primary">
            Start the system
          </Link>
          <Link to="/login">I already have an account</Link>
        </div>
      </section>

      <section className="pillars">
        <div>
          <h3>Obsession Loop</h3>
          <p>Convert tasks into points and rewards so consistency compounds.</p>
        </div>
        <div>
          <h3>Clarity by Design</h3>
          <p>Short lists, deliberate schedules, and no noise.</p>
        </div>
        <div>
          <h3>Momentum Truth</h3>
          <p>See streaks, completion, and progress with zero fluff.</p>
        </div>
      </section>

      <footer className="closing">SelfAxiom is self-hostable and built to stay fast.</footer>
    </div>
  );
}
