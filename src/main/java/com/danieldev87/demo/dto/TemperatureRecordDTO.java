package com.danieldev87.demo.dto;

import com.danieldev87.demo.domain.model.TemperatureLog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para el registro manual de temperatura en un lote de yogurt.
 * Contiene el valor de temperatura y el tipo de registro asociado.
 */
@Data
@Schema(description = "DTO para registrar una medición de temperatura en un lote de yogurt")
public class TemperatureRecordDTO {
    
    /**
     * Valor de temperatura medido en grados Celsius.
     * Ejemplo: 43.5 para temperatura de incubación
     */
    @Schema(description = "Temperatura medida en grados Celsius (°C)", 
            example = "43.5", 
            minimum = "0", 
            maximum = "100",
            required = true)
    private Double temperature;
    
    /**
     * Tipo de registro de temperatura.
     * Define en qué fase del proceso se tomó la medición.
     */
    @Schema(description = "Tipo de registro de temperatura según la fase del proceso", 
            example = "INCUBATION",
            allowableValues = {"HEATING", "COOLING", "INCUBATION", "REFRIGERATION", "MANUAL"},
            required = true)
    private TemperatureLog.LogType type;
}