import { batchStatusLabels, difficultyLabels, temperatureTypeLabels } from "../constants";
import type { BatchStatus, DifficultyLevel, TemperatureLogType } from "../types";

interface StatusBadgeProps {
  status: BatchStatus;
}

export function StatusBadge({ status }: StatusBadgeProps) {
  return <span className={`badge badge-${status.toLowerCase()}`}>{batchStatusLabels[status]}</span>;
}

interface DifficultyBadgeProps {
  difficulty: DifficultyLevel;
}

export function DifficultyBadge({ difficulty }: DifficultyBadgeProps) {
  return <span className="badge badge-neutral">{difficultyLabels[difficulty]}</span>;
}

interface TemperatureTypeBadgeProps {
  type: TemperatureLogType;
}

export function TemperatureTypeBadge({ type }: TemperatureTypeBadgeProps) {
  return <span className="badge badge-neutral">{temperatureTypeLabels[type]}</span>;
}
