import { apiClient } from "../api/client";
import { batchStatusLabels, batchStatuses } from "../constants";
import { useAsyncResource } from "../hooks/useAsyncResource";
import type { Dashboard, YogurtBatch } from "../types";
import { formatDateTime, formatNumber, getErrorMessage } from "../utils/format";
import { Notice } from "../components/Notice";
import { Panel } from "../components/Panel";
import { StatusBadge } from "../components/StatusBadge";

interface DashboardData {
  activeBatches: YogurtBatch[];
  dashboard: Dashboard;
}

interface DashboardPageProps {
  refreshKey: number;
}

export function DashboardPage({ refreshKey }: DashboardPageProps) {
  const { data, error, loading } = useAsyncResource<DashboardData>(
    async () => {
      const [dashboard, activeBatches] = await Promise.all([
        apiClient.getDashboard(),
        apiClient.getActiveBatches()
      ]);
      return { activeBatches, dashboard };
    },
    [refreshKey]
  );

  if (loading) {
    return <Notice message="Cargando metricas de produccion..." title="Consultando backend" />;
  }

  if (error) {
    return <Notice message={getErrorMessage(error)} title="No se pudo cargar el dashboard" tone="error" />;
  }

  if (!data) {
    return <Notice message="El backend no devolvio informacion para mostrar." title="Sin datos" />;
  }

  const totalBatches = batchStatuses.reduce((total, status) => total + (data.dashboard.batchCounts[status] ?? 0), 0);

  return (
    <div className="content-grid">
      <div className="metric-grid">
        <MetricCard label="Lotes totales" value={totalBatches} />
        <MetricCard label="Activos" value={data.dashboard.activeBatchesCount} />
        <MetricCard label="Completados hoy" value={data.dashboard.completedToday} />
        <MetricCard label="En seguimiento" value={data.activeBatches.length} />
      </div>

      <Panel eyebrow="Estados" title="Distribucion de lotes">
        <div className="status-grid">
          {batchStatuses.map((status) => {
            const count = data.dashboard.batchCounts[status] ?? 0;
            const percentage = totalBatches > 0 ? Math.round((count / totalBatches) * 100) : 0;
            return (
              <div className="status-row" key={status}>
                <div>
                  <StatusBadge status={status} />
                  <span>{batchStatusLabels[status]}</span>
                </div>
                <strong>{count}</strong>
                <div className="meter" aria-label={`${batchStatusLabels[status]} ${percentage}%`}>
                  <span style={{ width: `${percentage}%` }} />
                </div>
              </div>
            );
          })}
        </div>
      </Panel>

      <Panel eyebrow="Produccion activa" title="Lotes que requieren seguimiento">
        {data.activeBatches.length === 0 ? (
          <Notice message="No hay lotes activos en este momento." />
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Codigo</th>
                  <th>Receta</th>
                  <th>Estado</th>
                  <th>Inicio</th>
                  <th>Volumen</th>
                </tr>
              </thead>
              <tbody>
                {data.activeBatches.map((batch) => (
                  <tr key={batch.id}>
                    <td>{batch.batchCode}</td>
                    <td>{batch.recipe.name}</td>
                    <td>
                      <StatusBadge status={batch.status} />
                    </td>
                    <td>{formatDateTime(batch.startTime ?? batch.createdAt)}</td>
                    <td>{formatNumber(batch.milkVolume, 1)} L</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Panel>
    </div>
  );
}

interface MetricCardProps {
  label: string;
  value: number;
}

function MetricCard({ label, value }: MetricCardProps) {
  return (
    <article className="metric-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}
