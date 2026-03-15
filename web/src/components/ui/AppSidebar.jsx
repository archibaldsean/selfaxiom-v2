import { NavLink } from "react-router-dom";

const navItems = [
  { href: "/dashboard", label: "Dashboard", icon: "D" },
  { href: "/goals", label: "Goals", icon: "G" },
  { href: "/tasks", label: "Tasks", icon: "T" },
  { href: "/schedule", label: "Schedule", icon: "S" },
  { href: "/stats", label: "Stats / History", icon: "H" },
  { href: "/rewards", label: "Rewards", icon: "R" },
  { href: "/settings", label: "Settings", icon: "P" },
];

export default function AppSidebar() {
  return (
    <aside className="app-sidebar">
      <div className="app-sidebar-title">System</div>
      <nav className="app-nav">
        {navItems.map((item) => (
          <NavLink
            key={item.href}
            to={item.href}
            className={({ isActive }) => `app-nav-item${isActive ? " is-active" : ""}`}
          >
            <span className="app-nav-icon" aria-hidden="true">
              {item.icon}
            </span>
            <span className="app-nav-label">{item.label}</span>
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
