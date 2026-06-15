import { useMemo, useState } from "react";
import { Icon } from "./components/Icon";
import { apiClient } from "./api/client";

type ViewId = "dashboard" | "recipes" | "batches" | "monitoring";

const views: Array<{ id: ViewId; label: string; icon: Parameters<typeof Icon>[0]["name"] }> = [
  { id: "dashboard", label: "Dashboard", icon: "activity" },
  { id: "recipes", label: "Recetas", icon: "recipe" },
  { id: "batches", label: "Lotes", icon: "batch" },
  { id: "monitoring", label: "Temperaturas", icon: "temperature" }
];

function App() {
  const [activeView, setActiveView] = useState<ViewId>("dashboard");

  const currentTitle = useMemo(
    () => views.find((view) => view.id === activeView)?.label ?? "Dashboard",
    [activeView]
  );

  return (
    <div className="app-shell">
      <aside className="sidebar" aria-label="Navegacion principal">
        <div className="brand">
          <div className="brand-mark">YM</div>
          <div>
            <strong>Yogurt Maker</strong>
            <span>Panel de produccion</span>
          </div>
        </div>

        <nav className="nav-list">
          {views.map((view) => (
            <button
              key={view.id}
              className={`nav-item ${activeView === view.id ? "is-active" : ""}`}
              type="button"
              onClick={() => setActiveView(view.id)}
            >
              <Icon name={view.icon} />
              <span>{view.label}</span>
            </button>
          ))}
        </nav>
      </aside>

      <main className="workspace">
        <header className="topbar">
          <div>
            <span className="eyebrow">API conectada a {apiClient.baseUrl}</span>
            <h1>{currentTitle}</h1>
          </div>
          <button className="icon-button" type="button" title="Refrescar vista" aria-label="Refrescar vista">
            <Icon name="refresh" />
          </button>
        </header>

        <section className="empty-state">
          <Icon name={views.find((view) => view.id === activeView)?.icon ?? "activity"} size={38} />
          <h2>Base del panel lista</h2>
          <p>Las pantallas de operacion se conectaran al backend en el siguiente bloque.</p>
        </section>
      </main>
    </div>
  );
}

export default App;
