import type {
  ApiErrorBody,
  BatchStatus,
  Dashboard,
  Recipe,
  RecipePayload,
  StartBatchPayload,
  TemperatureLog,
  TemperatureRecordPayload,
  TemperatureSummary,
  YogurtBatch
} from "../types";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";

export class ApiError extends Error {
  status: number;
  details?: ApiErrorBody;

  constructor(message: string, status: number, details?: ApiErrorBody) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.details = details;
  }
}

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...init.headers
    },
    ...init
  });

  if (!response.ok) {
    let details: ApiErrorBody | undefined;
    try {
      details = (await response.json()) as ApiErrorBody;
    } catch {
      details = undefined;
    }
    throw new ApiError(details?.message ?? "No se pudo completar la solicitud", response.status, details);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  return text ? (JSON.parse(text) as T) : (undefined as T);
}

export const apiClient = {
  baseUrl: API_BASE_URL,

  getRecipes() {
    return request<Recipe[]>("/recipes");
  },

  searchRecipes(keyword: string) {
    return request<Recipe[]>(`/recipes/search?keyword=${encodeURIComponent(keyword)}`);
  },

  createRecipe(payload: RecipePayload) {
    return request<Recipe>("/recipes", {
      method: "POST",
      body: JSON.stringify(payload)
    });
  },

  updateRecipe(id: number, payload: RecipePayload) {
    return request<Recipe>(`/recipes/${id}`, {
      method: "PUT",
      body: JSON.stringify(payload)
    });
  },

  activateRecipe(id: number) {
    return request<void>(`/recipes/${id}/activate`, { method: "PATCH" });
  },

  deactivateRecipe(id: number) {
    return request<void>(`/recipes/${id}/deactivate`, { method: "PATCH" });
  },

  getBatches(status?: BatchStatus) {
    const query = status ? `?status=${status}` : "";
    return request<YogurtBatch[]>(`/batches${query}`);
  },

  getBatch(id: number) {
    return request<YogurtBatch>(`/batches/${id}`);
  },

  startBatch(payload: StartBatchPayload) {
    return request<YogurtBatch>("/batches", {
      method: "POST",
      body: JSON.stringify(payload)
    });
  },

  transitionBatch(id: number, action: "heating" | "inoculating" | "incubation" | "refrigeration" | "complete") {
    return request<YogurtBatch>(`/batches/${id}/${action}`, { method: "POST" });
  },

  failBatch(id: number, reason: string) {
    return request<YogurtBatch>(`/batches/${id}/fail`, {
      method: "POST",
      body: JSON.stringify({ reason })
    });
  },

  recordTemperature(id: number, payload: TemperatureRecordPayload) {
    return request<void>(`/batches/${id}/temperature`, {
      method: "POST",
      body: JSON.stringify(payload)
    });
  },

  getActiveBatches() {
    return request<YogurtBatch[]>("/monitoring/batches/active");
  },

  getTemperatureSummary(batchId: number) {
    return request<TemperatureSummary>(`/monitoring/batches/${batchId}/temperature`);
  },

  getTemperatureLogs(batchId: number, start?: string, end?: string) {
    const params = new URLSearchParams();
    if (start) params.set("start", start);
    if (end) params.set("end", end);
    const query = params.toString();
    return request<TemperatureLog[]>(`/monitoring/batches/${batchId}/temperature-logs${query ? `?${query}` : ""}`);
  },

  getDashboard() {
    return request<Dashboard>("/monitoring/dashboard");
  }
};
