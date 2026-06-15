import type { TemperatureLog } from "../types";
import { formatDateTime, formatTemperature } from "../utils/format";

interface TemperatureChartProps {
  logs: TemperatureLog[];
}

export function TemperatureChart({ logs }: TemperatureChartProps) {
  const sortedLogs = [...logs].sort(
    (left, right) => new Date(left.recordedAt).getTime() - new Date(right.recordedAt).getTime()
  );

  if (sortedLogs.length < 2) {
    return (
      <div className="chart-empty">
        <strong>Sin historial suficiente</strong>
        <span>Registra al menos dos lecturas para dibujar la curva.</span>
      </div>
    );
  }

  const values = sortedLogs.map((log) => log.temperature);
  const min = Math.min(...values);
  const max = Math.max(...values);
  const range = Math.max(max - min, 1);
  const width = 640;
  const height = 240;
  const padding = 28;
  const plotWidth = width - padding * 2;
  const plotHeight = height - padding * 2;

  const points = sortedLogs
    .map((log, index) => {
      const x = padding + (index / (sortedLogs.length - 1)) * plotWidth;
      const y = padding + (1 - (log.temperature - min) / range) * plotHeight;
      return `${x},${y}`;
    })
    .join(" ");

  const lastLog = sortedLogs[sortedLogs.length - 1];
  const firstLog = sortedLogs[0];

  return (
    <div className="temperature-chart">
      <svg aria-label="Grafica de temperatura del lote" role="img" viewBox={`0 0 ${width} ${height}`}>
        <line className="chart-grid-line" x1={padding} x2={width - padding} y1={padding} y2={padding} />
        <line className="chart-grid-line" x1={padding} x2={width - padding} y1={height / 2} y2={height / 2} />
        <line className="chart-grid-line" x1={padding} x2={width - padding} y1={height - padding} y2={height - padding} />
        <polyline className="chart-line" fill="none" points={points} />
        {sortedLogs.map((log, index) => {
          const x = padding + (index / (sortedLogs.length - 1)) * plotWidth;
          const y = padding + (1 - (log.temperature - min) / range) * plotHeight;
          return <circle className="chart-dot" cx={x} cy={y} key={log.id} r={index === sortedLogs.length - 1 ? 5 : 3} />;
        })}
      </svg>

      <div className="chart-scale">
        <span>{formatTemperature(max)}</span>
        <span>{formatTemperature(min)}</span>
      </div>

      <div className="chart-meta">
        <span>{formatDateTime(firstLog.recordedAt)}</span>
        <strong>Ultima: {formatTemperature(lastLog.temperature)}</strong>
        <span>{formatDateTime(lastLog.recordedAt)}</span>
      </div>
    </div>
  );
}
