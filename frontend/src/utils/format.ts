import type { ApiErrorBody } from "../types";

export function formatDateTime(value?: string | null) {
  if (!value) return "Sin fecha";

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;

  return new Intl.DateTimeFormat("es-CO", {
    dateStyle: "medium",
    timeStyle: "short"
  }).format(date);
}

export function formatNumber(value?: number | null, digits = 1) {
  if (value === null || value === undefined || Number.isNaN(value)) return "Sin dato";
  return new Intl.NumberFormat("es-CO", {
    minimumFractionDigits: digits,
    maximumFractionDigits: digits
  }).format(value);
}

export function formatTemperature(value?: number | null) {
  return value === null || value === undefined ? "Sin lectura" : `${formatNumber(value, 1)} °C`;
}

export function getErrorMessage(error: unknown) {
  if (error instanceof Error) return error.message;
  return "Ocurrio un error inesperado";
}

export function flattenApiErrors(details?: ApiErrorBody) {
  if (!details) return [];

  const fieldErrors = details.fields ? Object.entries(details.fields).map(([field, message]) => `${field}: ${message}`) : [];
  return [...fieldErrors, ...(details.errors ?? [])];
}
