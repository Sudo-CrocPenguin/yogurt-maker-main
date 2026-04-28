package com.danieldev87.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String name;
    
    /**
     * Cantidad necesaria del ingrediente.
     */
    @Schema(description = "Cantidad necesaria del ingrediente según la unidad especificada", 
            example = "2.0",
            required = true,
            minimum = "0")
    private Double quantity;
    
    /**
     * Unidad de medida del ingrediente.
     */
    @Schema(description = "Unidad de medida para la cantidad del ingrediente", 
            example = "litros",
            allowableValues = {"kg", "g", "ml", "litros", "cucharadas", "cucharaditas", "unidad", "taza", "pizca"},
            required = true)
    private String unit;
    
    /**
     * Notas adicionales sobre el ingrediente.
     */
    @Schema(description = "Notas o instrucciones especiales sobre este ingrediente (ej. 'sin lactosa', 'temperatura ambiente')", 
            example = "Usar leche sin lactosa para versión apta para intolerantes",
            maxLength = 200)
    private String notes;
    
    /**
     * Indica si el ingrediente es opcional.
     */
    @Schema(description = "Indica si el ingrediente es opcional (true) u obligatorio (false) para la receta", 
            example = "false",
            defaultValue = "false")
    private Boolean optional;
}