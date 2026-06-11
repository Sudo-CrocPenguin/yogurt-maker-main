package com.danieldev87.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Clase contenedora de DTOs relacionados con los lotes de producción de yogurt.
 * Incluye las solicitudes para iniciar un nuevo lote y para marcar un lote como fallido.
 */
public class BatchDTO {
    
    /**
     * DTO para la solicitud de inicio de un nuevo lote de producción.
     * Permite especificar la receta base y opcionalmente personalizar los volúmenes.
     */
    @Data
    @Schema(description = "Solicitud para iniciar un nuevo lote de producción de yogurt")
    public static class StartBatchRequest {
        
        /**
         * ID de la receta a utilizar como base.
         * Este campo es obligatorio para crear un nuevo lote.
         */
        @Schema(description = "ID único de la receta de yogurt a utilizar como base para el lote", 
                example = "1", 
                required = true)
        @NotNull(message = "El ID de la receta es obligatorio")
        private Long recipeId;
        
        /**
         * Volumen personalizado de leche en litros.
         * Si no se especifica, se usa el valor por defecto de la receta.
         */
        @Schema(description = "Cantidad personalizada de leche en litros. Si es null, se usa el valor por defecto de la receta.", 
                example = "2.5", 
                minimum = "0.1",
                nullable = true)
        @DecimalMin(value = "0.1", message = "El volumen personalizado de leche debe ser mayor o igual a 0.1 litros")
        private Double customMilkVolume;
        
        /**
         * Cantidad personalizada de fermento en cucharadas.
         * Si no se especifica, se usa el valor por defecto de la receta.
         */
        @Schema(description = "Cantidad personalizada de fermento en cucharadas. Si es null, se usa el valor por defecto de la receta.", 
                example = "3.0", 
                minimum = "0.5",
                nullable = true)
        @DecimalMin(value = "0.5", message = "La cantidad personalizada de fermento debe ser mayor o igual a 0.5 cucharadas")
        private Double customStarterAmount;
    }
    
    /**
     * DTO para la solicitud de marcado de lote como fallido.
     * Requiere especificar el motivo del fallo para trazabilidad.
     */
    @Data
    @Schema(description = "Solicitud para marcar un lote como fallido, especificando el motivo")
    public static class FailRequest {
        
        /**
         * Motivo por el cual el lote se marca como fallido.
         * Este texto se almacena en las notas del lote.
         */
        @Schema(description = "Razón o motivo detallado del fallo del lote (ej. 'Temperatura excedida durante incubación', 'Contaminación detectada')", 
                example = "La temperatura de incubación superó los 50°C, comprometiendo los cultivos.",
                required = true,
                minLength = 5)
        @NotBlank(message = "El motivo de fallo es obligatorio")
        @Size(min = 5, max = 500, message = "El motivo de fallo debe tener entre 5 y 500 caracteres")
        private String reason;
    }
}
