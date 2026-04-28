package com.danieldev87.demo.domain.model;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Entidad que representa un ingrediente en una receta de yogurt.
 * Cada ingrediente pertenece a una receta específica y puede ser opcional u obligatorio.
 */
@Entity
@Table(name = "ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ingrediente asociado a una receta de yogurt, con su cantidad, unidad y si es opcional")
public class Ingredient {
    
    /**
     * Identificador único autogenerado del ingrediente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único autogenerado del ingrediente en la base de datos", 
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    /**
     * Nombre descriptivo del ingrediente.
     */
    @Column(nullable = false)
    @Schema(description = "Nombre descriptivo del ingrediente (ej. 'Leche entera', 'Cultivo láctico')", 
            example = "Leche entera fresca",
            required = true,
            minLength = 2,
            maxLength = 100)
    private String name;
    
    /**
     * Cantidad necesaria del ingrediente según la unidad especificada.
     */
    @Schema(description = "Cantidad numérica del ingrediente necesaria para la receta", 
            example = "2.0",
            minimum = "0")
    private Double quantity;
    
    /**
     * Unidad de medida para la cantidad especificada.
     * Ejemplos: kg, g, ml, litros, cucharadas, etc.
     */
    @Schema(description = "Unidad de medida para la cantidad (kg, g, ml, litros, cucharadas, cucharaditas, unidad, taza, pizca)", 
            example = "litros",
            allowableValues = {"kg", "g", "ml", "litros", "cucharadas", "cucharaditas", "unidad", "taza", "pizca"})
    private String unit;
    
    /**
     * Receta a la que pertenece este ingrediente.
     * Relación muchos a uno con la entidad Recipe.
     */
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    @Schema(description = "Receta de yogurt a la que pertenece este ingrediente",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Recipe recipe;
    
    /**
     * Notas adicionales o instrucciones especiales sobre el ingrediente.
     */
    @Schema(description = "Notas adicionales o instrucciones especiales sobre el uso del ingrediente", 
            example = "Usar leche a temperatura ambiente para mejores resultados",
            maxLength = 200)
    private String notes;
    
    /**
     * Indica si el ingrediente es opcional en la receta.
     */
    @Column(nullable = false)
    @Schema(description = "Indica si el ingrediente es opcional (true) o es obligatorio (false) para la receta", 
            example = "false",
            defaultValue = "false")
    private Boolean optional;
}