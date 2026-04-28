package com.danieldev87.demo.domain.model;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que registra cada medición de temperatura durante el proceso
 * de producción de un lote de yogurt. Incluye el valor, tipo de registro
 * y la fecha/hora exacta de la medición.
 */
@Entity
@Table(name = "temperature_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Registro de temperatura tomado durante el proceso de producción de un lote de yogurt")
public class TemperatureLog {
    
    /**
     * Identificador único autogenerado del registro de temperatura.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único autogenerado del registro de temperatura", 
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    /**
     * Lote de yogurt al que pertenece este registro de temperatura.
     */
    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    @Schema(description = "Lote de yogurt al que corresponde esta medición de temperatura",
            accessMode = Schema.AccessMode.READ_ONLY)
    private YogurtBatch batch;
    
    /**
     * Valor de temperatura medido en grados Celsius.
     */
    @Column(nullable = false)
    @Schema(description = "Temperatura medida en grados Celsius (°C) en el momento del registro", 
            example = "43.5",
            required = true,
            minimum = "0",
            maximum = "100")
    private Double temperature;
    
    /**
     * Fecha y hora exacta en que se realizó la medición.
     */
    @Column(nullable = false)
    @Schema(description = "Fecha y hora exacta en que se registró esta medición de temperatura (formato ISO 8601)", 
            example = "2024-01-15T14:30:00",
            required = true)
    private LocalDateTime recordedAt;
    
    /**
     * Tipo de registro según la fase del proceso.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Fase del proceso durante la cual se tomó la temperatura", 
            example = "INCUBATION",
            allowableValues = {"HEATING", "COOLING", "INCUBATION", "REFRIGERATION", "MANUAL"},
            required = true)
    private LogType type;
    
    /**
     * Notas opcionales sobre la medición de temperatura.
     */
    @Schema(description = "Notas adicionales o comentarios sobre esta medición de temperatura (ej. 'Temperatura verificada con termómetro externo')", 
            example = "Lectura verificada con termómetro digital calibrado",
            maxLength = 200)
    private String notes;
    
    /**
     * Enumeración de los tipos de registro de temperatura disponibles.
     */
    @Schema(description = "Tipos de registro de temperatura según la fase del proceso de producción")
    public enum LogType {
        @Schema(description = "Registro durante la fase de calentamiento de la leche")
        HEATING, 
        @Schema(description = "Registro durante la fase de enfriamiento previo a la inoculación")
        COOLING, 
        @Schema(description = "Registro durante la fase de incubación con cultivos activos")
        INCUBATION, 
        @Schema(description = "Registro durante la fase de refrigeración final")
        REFRIGERATION, 
        @Schema(description = "Registro manual realizado por el operador en cualquier momento")
        MANUAL
    }
}