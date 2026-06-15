export type DifficultyLevel = "BEGINNER" | "INTERMEDIATE" | "ADVANCED";

export type BatchStatus =
  | "PREPARING"
  | "HEATING"
  | "COOLING"
  | "INOCULATING"
  | "INCUBATING"
  | "REFRIGERATING"
  | "COMPLETED"
  | "FAILED";

export type TemperatureLogType =
  | "HEATING"
  | "COOLING"
  | "INCUBATION"
  | "REFRIGERATION"
  | "MANUAL";

export interface Ingredient {
  id?: number;
  name: string;
  quantity: number;
  unit: string;
  notes?: string;
  optional?: boolean;
}

export interface Recipe {
  id: number;
  name: string;
  description?: string;
  ingredients: Ingredient[];
  defaultMilkVolume: number;
  defaultStarterAmount: number;
  heatingTemperature: number;
  heatingDuration: number;
  inoculationTemperature: number;
  incubationTemperature: number;
  minIncubationTime: number;
  maxIncubationTime: number;
  refrigerationTime: number;
  difficulty: DifficultyLevel;
  tips?: string;
  active: boolean;
}

export type RecipePayload = Omit<Recipe, "id" | "active">;

export interface TemperatureLog {
  id: number;
  temperature: number;
  recordedAt: string;
  type: TemperatureLogType;
  notes?: string;
}

export interface YogurtBatch {
  id: number;
  batchCode: string;
  recipe: Recipe;
  status: BatchStatus;
  milkVolume: number;
  starterAmount: number;
  targetTemperature: number;
  incubationTime: number;
  startTime?: string;
  incubationStartTime?: string;
  incubationEndTime?: string;
  refrigerationStartTime?: string;
  temperatureLogs?: TemperatureLog[];
  notes?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface StartBatchPayload {
  recipeId: number;
  customMilkVolume?: number;
  customStarterAmount?: number;
}

export interface TemperatureRecordPayload {
  temperature: number;
  type: TemperatureLogType;
}

export interface TemperatureSummary {
  currentTemperature: number | null;
  maximumTemperature: number | null;
  minimumTemperature: number | null;
  averageTemperature: number | null;
}

export interface Dashboard {
  batchCounts: Record<BatchStatus, number>;
  activeBatchesCount: number;
  completedToday: number;
}

export interface ApiErrorBody {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  fields?: Record<string, string>;
  errors?: string[];
}
