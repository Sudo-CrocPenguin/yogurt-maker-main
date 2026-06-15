import type { BatchStatus, DifficultyLevel, TemperatureLogType } from "./types";

export const batchStatuses: BatchStatus[] = [
  "PREPARING",
  "HEATING",
  "COOLING",
  "INOCULATING",
  "INCUBATING",
  "REFRIGERATING",
  "COMPLETED",
  "FAILED"
];

export const activeBatchStatuses: BatchStatus[] = [
  "PREPARING",
  "HEATING",
  "COOLING",
  "INOCULATING",
  "INCUBATING",
  "REFRIGERATING"
];

export const difficultyLevels: DifficultyLevel[] = ["BEGINNER", "INTERMEDIATE", "ADVANCED"];

export const temperatureLogTypes: TemperatureLogType[] = [
  "HEATING",
  "COOLING",
  "INCUBATION",
  "REFRIGERATION",
  "MANUAL"
];

export const batchStatusLabels: Record<BatchStatus, string> = {
  PREPARING: "Preparando",
  HEATING: "Calentando",
  COOLING: "Enfriando",
  INOCULATING: "Inoculando",
  INCUBATING: "Incubando",
  REFRIGERATING: "Refrigerando",
  COMPLETED: "Completado",
  FAILED: "Fallido"
};

export const difficultyLabels: Record<DifficultyLevel, string> = {
  BEGINNER: "Principiante",
  INTERMEDIATE: "Intermedio",
  ADVANCED: "Avanzado"
};

export const temperatureTypeLabels: Record<TemperatureLogType, string> = {
  HEATING: "Calentamiento",
  COOLING: "Enfriamiento",
  INCUBATION: "Incubacion",
  REFRIGERATION: "Refrigeracion",
  MANUAL: "Manual"
};
