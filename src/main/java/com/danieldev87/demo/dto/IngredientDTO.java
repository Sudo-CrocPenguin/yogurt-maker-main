package com.danieldev87.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un ingrediente dentro de una receta.
 * Cada ingrediente tiene un nombre, cantidad, unidad de medida y puede ser opcional.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ingrediente de una receta de yogurt con su cantidad y unidad de medida")
public class IngredientDTO {
    
    /**
     * Nombre del ingrediente.
     */
    @Schema(description = "Nombre descriptivo del ingrediente", 
            example = "Leche entera fresca", 
            required = true,
            minLength = 2,
            maxLength = 100)
    @NotBlank(message = "El nombre del ingrediente es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre del ingrediente debe tener entre 2 y 100 caracteres")
    private String name;
    
    /**
     * Cantidad necesaria del ingrediente.
     */
    @Schema(description = "Cantidad necesaria del ingrediente según la unidad especificada", 
            example = "2.0",
            required = true,
            minimum = "0")
    @NotNull(message = "La cantidad del ingrediente es obligatoria")
    @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad del ingrediente debe ser mayor que cero")
    private Double quantity;
    
    /**
     * Unidad de medida del ingrediente.
     */
    @Schema(description = "Unidad de medida para la cantidad del ingrediente", 
            example = "litros",
            allowableValues = {"kg", "g", "ml", "litros", "cucharadas", "cucharaditas", "unidad", "taza", "pizca"},
            required = true)
    @NotBlank(message = "La unidad del ingrediente es obligatoria")
    @Size(max = 30, message = "La unidad del ingrediente no puede superar 30 caracteres")
    private String unit;
    
    /**
     * Notas adicionales sobre el ingrediente.
     */
    @Schema(description = "Notas o instrucciones especiales sobre este ingrediente (ej. 'sin lactosa', 'temperatura ambiente')", 
            example = "Usar leche sin lactosa para versión apta para intolerantes",
            maxLength = 200)
    @Size(max = 200, message = "Las notas del ingrediente no pueden superar 200 caracteres")
    private String notes;
    
    /**
     * Indica si el ingrediente es opcional.
     */
    @Schema(description = "Indica si el ingrediente es opcional (true) u obligatorio (false) para la receta", 
            example = "false",
            defaultValue = "false")
    private Boolean optional;
}
