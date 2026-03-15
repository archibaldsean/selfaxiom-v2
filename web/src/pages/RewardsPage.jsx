export default function RewardsPage() {
  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Rewards</h1>
          <p>Earned, not given.</p>
        </div>
      </header>

      <section className="rewards-balance">
        <div className="rewards-balance-card">
          <div className="stat-label">Points balance</div>
          <div className="stat-value">1,240</div>
        </div>
        <div className="rewards-balance-card">
          <div className="stat-label">Last redemption</div>
          <div className="stat-value">3 days ago</div>
        </div>
      </section>

      <section className="rewards-grid">
        <div className="reward-card">
          <h3>New running shoes</h3>
          <div className="reward-meta">Cost: 800 points</div>
        </div>
        <div className="reward-card">
          <h3>Weekend reset</h3>
          <div className="reward-meta">Cost: 1200 points</div>
        </div>
        <div className="reward-card">
          <h3>Course upgrade</h3>
          <div className="reward-meta">Cost: 500 points</div>
        </div>
      </section>

      <section className="rewards-history">
        <div className="section-title">Redemption history</div>
        <div className="momentum-placeholder" />
      </section>
    </div>
  );
}
