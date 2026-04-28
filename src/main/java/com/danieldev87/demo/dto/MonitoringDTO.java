package com.danieldev87.demo.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Clase contenedora de DTOs relacionados con el monitoreo de la producción.
 * Incluye resúmenes de temperatura y datos del dashboard general.
 */
public class MonitoringDTO {
    
    /**
     * DTO que representa un resumen de temperaturas para un lote específico.
     * Incluye valores actuales, máximos, mínimos y promedio.
     */
    @Data
    @Builder
    @Schema(description = "Resumen de mediciones de temperatura para un lote de yogurt")
    public static class TemperatureSummary {
        
        /**
         * Temperatura actual del lote en grados Celsius.
         */
        @Schema(description = "Temperatura más reciente registrada para el lote en °C. Null si no hay registros.", 
                example = "43.2",
                nullable = true)
        private Double currentTemperature;
        
        /**
         * Temperatura máxima registrada para el lote.
         */
        @Schema(description = "Temperatura máxima alcanzada durante todo el proceso en °C", 
                example = "85.5")
        private Double maximumTemperature;
        
        /**
         * Temperatura mínima registrada para el lote.
         */
        @Schema(description = "Temperatura mínima registrada durante todo el proceso en °C", 
                example = "20.0")
        private Double minimumTemperature;
        
        /**
         * Temperatura promedio durante la incubación.
         */
        @Schema(description = "Temperatura promedio calculada durante la fase de incubación en °C", 
                example = "43.0")
        private Double averageTemperature;
    }
    
    /**
     * DTO que representa el dashboard general del sistema.
     * Contiene métricas consolidadas de todos los lotes.
     */
    @Data
    @Builder
    @Schema(description = "Dashboard con métricas generales del sistema de producción de yogurt")
    public static class Dashboard {
        
        /**
         * Mapa con el conteo de lotes por cada estado.
         * Llave: nombre del estado, Valor: cantidad de lotes.
         */
        @Schema(description = "Mapa con la cantidad de lotes agrupados por estado (PREPARING, HEATING, COOLING, INCUBATING, REFRIGERATING, COMPLETED, FAILED)", 
                example = "{\"PREPARING\": 2, \"HEATING\": 1, \"INCUBATING\": 3, \"COMPLETED\": 15, \"FAILED\": 1}")
        private Map<String, Long> batchCounts;
        
        /**
         * Total de lotes activos (todos excepto COMPLETED y FAILED).
         */
        @Schema(description = "Cantidad total de lotes que están actualmente en producción (todos los estados excepto COMPLETED y FAILED)", 
                example = "6")
        private Long activeBatchesCount;
        
        /**
         * Cantidad de lotes completados en el día actual.
         */
        @Schema(description = "Número de lotes que se completaron exitosamente durante el día de hoy", 
                example = "3")
        private Integer completedToday;
    }
}