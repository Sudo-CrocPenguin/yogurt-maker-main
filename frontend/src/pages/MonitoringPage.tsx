import { useEffect, useState } from "react";
import { apiClient } from "../api/client";
import { batchStatusLabels } from "../constants";
import { Notice } from "../components/Notice";
import { Panel } from "../components/Panel";
import { SelectField } from "../components/Field";
import { StatusBadge, TemperatureTypeBadge } from "../components/StatusBadge";
import { TemperatureChart } from "../components/TemperatureChart";
import { useAsyncResource } from "../hooks/useAsyncResource";
import type { TemperatureLog, TemperatureSummary, YogurtBatch } from "../types";
import { formatDateTime, formatTemperature, getErrorMessage } from "../utils/format";

interface MonitoringPageProps {
  refreshKey: number;
}

interface MonitoringDetail {
  logs: TemperatureLog[];
  summary: TemperatureSummary;
}

export function MonitoringPage({ refreshKey }: MonitoringPageProps) {
  const [selectedBatchId, setSelectedBatchId] = useState("");

  const batchesResource = useAsyncResource<YogurtBatch[]>(() => apiClient.getBatches(), [refreshKey]);
  const batches = batchesResource.data ?? [];

  useEffect(() => {
    if (!selectedBatchId && batches.length > 0) {
      setSelectedBatchId(String(batches[0].id));
    }
  }, [batches, selectedBatchId]);

  const selectedBatch = batches.find((batch) => String(batch.id) === selectedBatchId) ?? null;

  const detailResource = useAsyncResource<MonitoringDetail | null>(
    async () => {
      if (!selectedBatchId) return null;
      const batchId = Number(selectedBatchId);
      const [summary, logs] = await Promise.all([
        apiClient.getTemperatureSummary(batchId),
        apiClient.getTemperatureLogs(batchId)
      ]);
      return { logs, summary };
    },
    [selectedBatchId, refreshKey]
  );

  if (batchesResource.loading) {
    return <Notice message="Cargando lotes para monitoreo..." title="Consultando backend" />;
  }

  if (batchesResource.error) {
    return <Notice message={getErrorMessage(batchesResource.error)} title="No se pudieron cargar lotes" tone="error" />;
  }

  if (batches.length === 0) {
    return <Notice message="Crea un lote para comenzar a monitorear temperaturas." title="Sin lotes" />;
  }

  return (
    <div className="content-grid">
      <Panel eyebrow="Seleccion" title="Lote monitoreado">
        <div className="monitoring-selector">
          <SelectField label="Lote" onChange={(event) => setSelectedBatchId(event.target.value)} value={selectedBatchId}>
            {batches.map((batch) => (
              <option key={batch.id} value={batch.id}>
                {batch.batchCode} - {batch.recipe.name} - {batchStatusLabels[batch.status]}
              </option>
            ))}
          </SelectField>
          {selectedBatch ? <StatusBadge status={selectedBatch.status} /> : null}
        </div>
      </Panel>

      {detailResource.loading ? <Notice message="Cargando registros de temperatura..." /> : null}
      {detailResource.error ? (
        <Notice message={getErrorMessage(detailResource.error)} title="No se pudo cargar el historial" tone="error" />
      ) : null}

      {detailResource.data ? (
        <>
          <Panel eyebrow="Resumen termico" title={selectedBatch?.recipe.name ?? "Temperatura"}>
            <div className="fact-grid">
              <Fact label="Actual" value={formatTemperature(detailResource.data.summary.currentTemperature)} />
              <Fact label="Maxima" value={formatTemperature(detailResource.data.summary.maximumTemperature)} />
              <Fact label="Minima" value={formatTemperature(detailResource.data.summary.minimumTemperature)} />
              <Fact label="Promedio incubacion" value={formatTemperature(detailResource.data.summary.averageTemperature)} />
            </div>
            <TemperatureChart logs={detailResource.data.logs} />
          </Panel>

          <Panel eyebrow="Historial" title="Registros de temperatura">
            {detailResource.data.logs.length === 0 ? (
              <Notice message="Este lote todavia no tiene registros de temperatura." />
            ) : (
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Fecha</th>
                      <th>Tipo</th>
                      <th>Temperatura</th>
                      <th>Notas</th>
                    </tr>
                  </thead>
                  <tbody>
                    {detailResource.data.logs.map((log) => (
                      <tr key={log.id}>
                        <td>{formatDateTime(log.recordedAt)}</td>
                        <td>
                          <TemperatureTypeBadge type={log.type} />
                        </td>
                        <td>{formatTemperature(log.temperature)}</td>
                        <td>{log.notes || "Sin notas"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </Panel>
        </>
      ) : null}
    </div>
  );
}

interface FactProps {
  label: string;
  value: string;
}

function Fact({ label, value }: FactProps) {
  return (
    <div className="fact-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}
