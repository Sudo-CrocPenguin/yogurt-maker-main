import { FormEvent, useMemo, useState } from "react";
import { apiClient, ApiError } from "../api/client";
import { batchStatusLabels, batchStatuses, temperatureLogTypes, temperatureTypeLabels } from "../constants";
import { Button } from "../components/Button";
import { Icon } from "../components/Icon";
import { InputField, SelectField } from "../components/Field";
import { Notice } from "../components/Notice";
import { Panel } from "../components/Panel";
import { StatusBadge, TemperatureTypeBadge } from "../components/StatusBadge";
import { useAsyncResource } from "../hooks/useAsyncResource";
import type { BatchStatus, Recipe, TemperatureLogType, TemperatureSummary, YogurtBatch } from "../types";
import { flattenApiErrors, formatDateTime, formatNumber, formatTemperature, getErrorMessage } from "../utils/format";

interface BatchesPageProps {
  refreshKey: number;
}

interface BatchPageData {
  batches: YogurtBatch[];
  recipes: Recipe[];
}

interface BatchDetailData {
  batch: YogurtBatch;
  summary: TemperatureSummary;
}

const transitionByStatus: Partial<Record<BatchStatus, { action: Parameters<typeof apiClient.transitionBatch>[1]; label: string }>> = {
  PREPARING: { action: "heating", label: "Iniciar calentamiento" },
  COOLING: { action: "inoculating", label: "Iniciar inoculacion" },
  INOCULATING: { action: "incubation", label: "Iniciar incubacion" },
  INCUBATING: { action: "refrigeration", label: "Iniciar refrigeracion" },
  REFRIGERATING: { action: "complete", label: "Completar lote" }
};

export function BatchesPage({ refreshKey }: BatchesPageProps) {
  const [statusFilter, setStatusFilter] = useState<BatchStatus | "">("");
  const [selectedBatchId, setSelectedBatchId] = useState<number | null>(null);
  const [detailRefreshKey, setDetailRefreshKey] = useState(0);
  const [recipeId, setRecipeId] = useState("");
  const [customMilkVolume, setCustomMilkVolume] = useState("");
  const [customStarterAmount, setCustomStarterAmount] = useState("");
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const { data, error, loading, reload } = useAsyncResource<BatchPageData>(
    async () => {
      const [batches, recipes] = await Promise.all([
        apiClient.getBatches(statusFilter || undefined),
        apiClient.getRecipes()
      ]);
      return { batches, recipes };
    },
    [statusFilter, refreshKey]
  );

  const batches = data?.batches ?? [];
  const recipes = data?.recipes ?? [];

  const selectedBatch = useMemo(
    () => batches.find((batch) => batch.id === selectedBatchId) ?? null,
    [batches, selectedBatchId]
  );

  async function createBatch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setMessage(null);
    setErrorMessage(null);

    try {
      const batch = await apiClient.startBatch({
        recipeId: Number(recipeId),
        customMilkVolume: customMilkVolume ? Number(customMilkVolume) : undefined,
        customStarterAmount: customStarterAmount ? Number(customStarterAmount) : undefined
      });
      setRecipeId("");
      setCustomMilkVolume("");
      setCustomStarterAmount("");
      setSelectedBatchId(batch.id);
      setMessage(`Lote ${batch.batchCode} creado en estado ${batchStatusLabels[batch.status]}.`);
      await reload();
      setDetailRefreshKey((current) => current + 1);
    } catch (error) {
      setErrorMessage(buildErrorMessage(error));
    } finally {
      setSaving(false);
    }
  }

  async function afterDetailMutation(successMessage: string) {
    setMessage(successMessage);
    await reload();
    setDetailRefreshKey((current) => current + 1);
  }

  return (
    <div className="content-grid">
      {message ? <Notice message={message} tone="success" /> : null}
      {errorMessage ? <Notice message={errorMessage} title="Operacion no completada" tone="error" /> : null}

      <div className="split-layout batches-layout">
        <div className="content-grid">
          <Panel eyebrow="Nuevo lote" title="Iniciar produccion">
            <form className="recipe-form" onSubmit={(event) => void createBatch(event)}>
              <SelectField label="Receta" onChange={(event) => setRecipeId(event.target.value)} required value={recipeId}>
                <option value="">Selecciona una receta</option>
                {recipes.map((recipe) => (
                  <option key={recipe.id} value={recipe.id}>
                    {recipe.name}
                  </option>
                ))}
              </SelectField>
              <div className="form-grid">
                <InputField
                  hint="Opcional. Si queda vacio usa la receta."
                  label="Leche personalizada"
                  min="0.1"
                  onChange={(event) => setCustomMilkVolume(event.target.value)}
                  step="0.1"
                  type="number"
                  value={customMilkVolume}
                />
                <InputField
                  hint="Opcional. Si queda vacio usa la receta."
                  label="Fermento personalizado"
                  min="0.5"
                  onChange={(event) => setCustomStarterAmount(event.target.value)}
                  step="0.1"
                  type="number"
                  value={customStarterAmount}
                />
              </div>
              <Button disabled={saving || recipes.length === 0} icon={<Icon name="plus" />} type="submit" variant="primary">
                {saving ? "Creando..." : "Crear lote"}
              </Button>
            </form>
          </Panel>

          <Panel eyebrow="Produccion" title="Lotes registrados">
            <div className="toolbar">
              <SelectField
                label="Filtrar por estado"
                onChange={(event) => setStatusFilter(event.target.value as BatchStatus | "")}
                value={statusFilter}
              >
                <option value="">Todos los estados</option>
                {batchStatuses.map((status) => (
                  <option key={status} value={status}>
                    {batchStatusLabels[status]}
                  </option>
                ))}
              </SelectField>
            </div>

            {loading ? <Notice message="Cargando lotes..." /> : null}
            {error ? <Notice message={getErrorMessage(error)} title="Error al cargar lotes" tone="error" /> : null}
            {!loading && !error && batches.length === 0 ? <Notice message="No hay lotes para este filtro." /> : null}

            <div className="card-list">
              {batches.map((batch) => (
                <button
                  className={`batch-card ${selectedBatchId === batch.id ? "is-selected" : ""}`}
                  key={batch.id}
                  onClick={() => setSelectedBatchId(batch.id)}
                  type="button"
                >
                  <span>{batch.batchCode}</span>
                  <strong>{batch.recipe.name}</strong>
                  <StatusBadge status={batch.status} />
                  <small>{formatDateTime(batch.createdAt)}</small>
                </button>
              ))}
            </div>
          </Panel>
        </div>

        <BatchDetail
          batchId={selectedBatchId}
          fallbackBatch={selectedBatch}
          onError={setErrorMessage}
          onMutation={afterDetailMutation}
          refreshKey={detailRefreshKey + refreshKey}
        />
      </div>
    </div>
  );
}

interface BatchDetailProps {
  batchId: number | null;
  fallbackBatch: YogurtBatch | null;
  onError: (message: string | null) => void;
  onMutation: (message: string) => Promise<void>;
  refreshKey: number;
}

function BatchDetail({ batchId, fallbackBatch, onError, onMutation, refreshKey }: BatchDetailProps) {
  const [failureReason, setFailureReason] = useState("");
  const [temperature, setTemperature] = useState("");
  const [temperatureType, setTemperatureType] = useState<TemperatureLogType>("MANUAL");
  const [busy, setBusy] = useState(false);

  const { data, error, loading } = useAsyncResource<BatchDetailData | null>(
    async () => {
      if (!batchId) return null;
      const [batch, summary] = await Promise.all([
        apiClient.getBatch(batchId),
        apiClient.getTemperatureSummary(batchId)
      ]);
      return { batch, summary };
    },
    [batchId, refreshKey]
  );

  const batch = data?.batch ?? fallbackBatch;
  const nextTransition = batch ? transitionByStatus[batch.status] : undefined;

  async function runTransition() {
    if (!batch || !nextTransition) return;
    setBusy(true);
    onError(null);
    try {
      const updated = await apiClient.transitionBatch(batch.id, nextTransition.action);
      await onMutation(`Lote ${updated.batchCode} actualizado a ${batchStatusLabels[updated.status]}.`);
    } catch (error) {
      onError(buildErrorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  async function failBatch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!batch) return;
    setBusy(true);
    onError(null);
    try {
      const updated = await apiClient.failBatch(batch.id, failureReason);
      setFailureReason("");
      await onMutation(`Lote ${updated.batchCode} marcado como fallido.`);
    } catch (error) {
      onError(buildErrorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  async function submitTemperature(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!batch) return;
    setBusy(true);
    onError(null);
    try {
      await apiClient.recordTemperature(batch.id, {
        temperature: Number(temperature),
        type: temperatureType
      });
      setTemperature("");
      await onMutation("Temperatura registrada correctamente.");
    } catch (error) {
      onError(buildErrorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  if (!batchId) {
    return (
      <Panel eyebrow="Detalle" title="Selecciona un lote">
        <Notice message="Elige un lote de la lista para ver acciones, tiempos y temperatura." />
      </Panel>
    );
  }

  if (loading && !batch) {
    return (
      <Panel eyebrow="Detalle" title="Cargando lote">
        <Notice message="Consultando detalle del lote..." />
      </Panel>
    );
  }

  if (error && !batch) {
    return (
      <Panel eyebrow="Detalle" title="Error">
        <Notice message={getErrorMessage(error)} tone="error" />
      </Panel>
    );
  }

  if (!batch) {
    return (
      <Panel eyebrow="Detalle" title="Sin lote">
        <Notice message="No se encontro el lote seleccionado." />
      </Panel>
    );
  }

  return (
    <div className="content-grid">
      <Panel actions={<StatusBadge status={batch.status} />} eyebrow={batch.batchCode} title={batch.recipe.name}>
        <div className="fact-grid">
          <Fact label="Leche" value={`${formatNumber(batch.milkVolume, 1)} L`} />
          <Fact label="Fermento" value={`${formatNumber(batch.starterAmount, 1)} cucharadas`} />
          <Fact label="Objetivo" value={formatTemperature(batch.targetTemperature)} />
          <Fact label="Incubacion" value={`${batch.incubationTime} h`} />
        </div>

        <div className="timeline">
          <TimelineItem label="Inicio" value={formatDateTime(batch.startTime ?? batch.createdAt)} />
          <TimelineItem label="Inicio incubacion" value={formatDateTime(batch.incubationStartTime)} />
          <TimelineItem label="Fin incubacion" value={formatDateTime(batch.incubationEndTime)} />
          <TimelineItem label="Inicio refrigeracion" value={formatDateTime(batch.refrigerationStartTime)} />
        </div>

        {batch.notes ? <Notice message={batch.notes} title="Notas del lote" /> : null}
      </Panel>

      <Panel eyebrow="Acciones" title="Control del proceso">
        <div className="action-grid">
          <Button
            disabled={!nextTransition || busy}
            icon={<Icon name="activity" />}
            onClick={() => void runTransition()}
            variant="primary"
          >
            {nextTransition?.label ?? "Sin transicion disponible"}
          </Button>
        </div>

        <form className="inline-form" onSubmit={(event) => void failBatch(event)}>
          <InputField
            label="Motivo de fallo"
            minLength={5}
            onChange={(event) => setFailureReason(event.target.value)}
            placeholder="Describe el problema"
            value={failureReason}
          />
          <Button disabled={busy || failureReason.trim().length < 5} icon={<Icon name="alert" />} type="submit" variant="danger">
            Marcar fallido
          </Button>
        </form>
      </Panel>

      <Panel eyebrow="Temperatura" title="Lectura actual y registro manual">
        <div className="fact-grid">
          <Fact label="Actual" value={formatTemperature(data?.summary.currentTemperature)} />
          <Fact label="Maxima" value={formatTemperature(data?.summary.maximumTemperature)} />
          <Fact label="Minima" value={formatTemperature(data?.summary.minimumTemperature)} />
          <Fact label="Promedio incubacion" value={formatTemperature(data?.summary.averageTemperature)} />
        </div>

        <form className="inline-form" onSubmit={(event) => void submitTemperature(event)}>
          <InputField
            label="Temperatura"
            max="100"
            min="0"
            onChange={(event) => setTemperature(event.target.value)}
            required
            step="0.1"
            type="number"
            value={temperature}
          />
          <SelectField
            label="Tipo"
            onChange={(event) => setTemperatureType(event.target.value as TemperatureLogType)}
            value={temperatureType}
          >
            {temperatureLogTypes.map((type) => (
              <option key={type} value={type}>
                {temperatureTypeLabels[type]}
              </option>
            ))}
          </SelectField>
          <Button disabled={busy || !temperature} icon={<Icon name="temperature" />} type="submit" variant="primary">
            Registrar
          </Button>
        </form>

        {batch.temperatureLogs?.length ? (
          <div className="recent-logs">
            {batch.temperatureLogs.slice(-5).map((log) => (
              <div key={log.id}>
                <TemperatureTypeBadge type={log.type} />
                <strong>{formatTemperature(log.temperature)}</strong>
                <span>{formatDateTime(log.recordedAt)}</span>
              </div>
            ))}
          </div>
        ) : null}
      </Panel>
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

function TimelineItem({ label, value }: FactProps) {
  return (
    <div className="timeline-item">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function buildErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    const details = flattenApiErrors(error.details);
    return [error.message, ...details].filter(Boolean).join(" | ");
  }
  return getErrorMessage(error);
}
