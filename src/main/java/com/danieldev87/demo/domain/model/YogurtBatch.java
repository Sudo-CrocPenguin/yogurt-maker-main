package com.danieldev87.demo.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad principal que representa un lote de producción de yogurt.
 * Contiene toda la información del proceso: receta utilizada, estado actual,
 * volúmenes, fechas de cada etapa y registros de temperatura asociados.
 */
@Entity
@Table(name = "yogurt_batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Lote de producción de yogurt con toda la información del proceso: receta, estado, volúmenes, fechas y registros de temperatura")
public class YogurtBatch {
    
    /**
     * Identificador único autogenerado del lote.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único autogenerado del lote en la base de datos", 
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    /**
     * Código único del lote generado automáticamente.
     * Formato: YB-{timestamp}
     */
    @Column(nullable = false)
    @Schema(description = "Código único del lote generado automáticamente con formato YB-{timestamp}", 
            example = "YB-1705324800000",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String batchCode;
    
    /**
     * Receta utilizada como base para este lote.
     */
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    @Schema(description = "Receta de yogurt utilizada como base para este lote de producción",
            required = true)
    private Recipe recipe;
    
    /**
     * Estado actual del lote en el ciclo de producción.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Estado actual del lote en el ciclo de producción de yogurt", 
            example = "INCUBATING",
            allowableValues = {"PREPARING", "HEATING", "COOLING", "INOCULATING", "INCUBATING", "REFIRGERATING", "COMPLETED", "FAILED"},
            required = true)
    private BatchStatus status;
    
    /**
     * Volumen de leche utilizado en litros.
     */
    @Column(nullable = false)
    @Schema(description = "Volumen de leche utilizado en este lote (litros)", 
            example = "2.5",
            required = true,
            minimum = "0.1")
    private Double milkVolume;
    
    /**
     * Cantidad de fermento utilizado en cucharadas.
     */
    @Column(nullable = false)
    @Schema(description = "Cantidad de fermento (cultivo iniciador) utilizado en este lote (cucharadas)", 
            example = "2.0",
            required = true,
            minimum = "0.5")
    private Double starterAmount;
    
    /**
     * Temperatura objetivo definida para el proceso.
     */
    @Column(nullable = false)
    @Schema(description = "Temperatura objetivo en °C para este lote (generalmente la temperatura de inoculación)", 
            example = "43.0",
            required = true)
    private Double targetTemperature;
    
    /**
     * Tiempo de incubación configurado para este lote en horas.
     */
    @Column(nullable = false)
    @Schema(description = "Tiempo de incubación configurado para este lote en horas", 
            example = "8",
            required = true,
            minimum = "4")
    private Integer incubationTime;
    
    /**
     * Fecha y hora de inicio del lote.
     */
    @Schema(description = "Fecha y hora en que se inició el lote de producción (formato ISO 8601)", 
            example = "2024-01-15T10:00:00")
    private LocalDateTime startTime;
    
    /**
     * Fecha y hora de inicio de la incubación.
     */
    @Schema(description = "Fecha y hora en que comenzó la fase de incubación (formato ISO 8601)", 
            example = "2024-01-15T12:30:00")
    private LocalDateTime incubationStartTime;
    
    /**
     * Fecha y hora calculada de finalización de la incubación.
     */
    @Schema(description = "Fecha y hora programada para la finalización de la incubación (formato ISO 8601)", 
            example = "2024-01-15T20:30:00")
    private LocalDateTime incubationEndTime;
    
    /**
     * Fecha y hora de inicio de la refrigeración.
     */
    @Schema(description = "Fecha y hora en que comenzó la fase de refrigeración (formato ISO 8601)", 
            example = "2024-01-15T21:00:00")
    private LocalDateTime refrigerationStartTime;
    
    /**
     * Lista de registros de temperatura asociados a este lote.
     */
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
    @Builder.Default
    @Schema(description = "Historial de registros de temperatura tomados durante el proceso de producción",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<TemperatureLog> temperatureLogs = new ArrayList<>();
    
    /**
     * Notas generales sobre el lote.
     */
    @Schema(description = "Notas generales o comentarios sobre el lote (ej. motivo de fallo, observaciones especiales)", 
            example = "Lote preparado con leche orgánica certificada",
            maxLength = 500)
    private String notes;
    
    /**
     * Fecha y hora de creación del registro en el sistema.
     */
    @Column(nullable = false)
    @Schema(description = "Fecha y hora en que se creó el registro del lote en el sistema (formato ISO 8601)", 
            example = "2024-01-15T10:00:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    /**
     * Fecha y hora de la última actualización del registro.
     */
    @Schema(description = "Fecha y hora de la última modificación del lote (formato ISO 8601)", 
            example = "2024-01-15T14:30:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    /**
     * Callback JPA que se ejecuta antes de persistir la entidad por primera vez.
     * Establece la fecha de creación y genera el código único del lote.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        batchCode = "YB-" + System.currentTimeMillis();
    }
    
    /**
     * Callback JPA que se ejecuta antes de actualizar la entidad.
     * Actualiza la fecha de modificación.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Enumeración de los estados posibles de un lote de producción.
     */
    @Schema(description = "Estados del ciclo de producción de un lote de yogurt")
    public enum BatchStatus {
        @Schema(description = "Lote recién creado, en preparación inicial")
        PREPARING,
        @Schema(description = "Fase de calentamiento de la leche a la temperatura objetivo")
        HEATING,
        @Schema(description = "Fase de enfriamiento hasta la temperatura de inoculación")
        COOLING,
        @Schema(description = "Fase de adición del cultivo iniciador (fermento)")
        INOCULATING,
        @Schema(description = "Fase de incubación con control de temperatura constante")
        INCUBATING,
        @Schema(description = "Fase de refrigeración final para detener la fermentación")
        REFRIGERATING,
        @Schema(description = "Lote completado exitosamente, yogurt listo para consumo")
        COMPLETED,
        @Schema(description = "Lote fallido por alguna razón registrada en las notas")
        FAILED
    }
}