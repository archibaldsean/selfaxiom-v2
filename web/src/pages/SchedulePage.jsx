export default function SchedulePage() {
  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Schedule</h1>
          <p>Turn ambition into time blocks.</p>
        </div>
        <button className="primary-button" type="button">
          Add block
        </button>
      </header>

      <div className="schedule-tabs">
        <button className="tab is-active" type="button">
          Daily
        </button>
        <button className="tab" type="button">
          Weekly
        </button>
      </div>

      <section className="schedule-grid">
        <div className="schedule-column">
          <div className="schedule-hour">06:00</div>
          <div className="schedule-block">Morning training</div>
          <div className="schedule-hour">09:00</div>
          <div className="schedule-block">Deep work: SelfAxiom</div>
          <div className="schedule-hour">13:00</div>
          <div className="schedule-block muted">Open slot</div>
        </div>
        <div className="schedule-column">
          <div className="schedule-hour">16:00</div>
          <div className="schedule-block">Admin + review</div>
          <div className="schedule-hour">19:00</div>
          <div className="schedule-block">Stretch + recovery</div>
          <div className="schedule-hour">21:00</div>
          <div className="schedule-block muted">Shutdown ritual</div>
        </div>
      </section>
    </div>
  );
}
