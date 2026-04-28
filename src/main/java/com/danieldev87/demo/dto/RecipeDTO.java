package com.danieldev87.demo.dto;

import java.util.List;

import com.danieldev87.demo.domain.model.Recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación y actualización de recetas de yogurt.
 * Contiene todos los parámetros necesarios para definir una receta completa,
 * incluyendo ingredientes, temperaturas, tiempos y nivel de dificultad.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos completos para crear o actualizar una receta de yogurt")
public class RecipeDTO {
    
    /**
     * Nombre único de la receta.
     */
    @Schema(description = "Nombre único de la receta de yogurt (debe ser descriptivo y no repetirse)", 
            example = "Yogurt Griego Natural", 
            required = true,
            minLength = 3,
            maxLength = 100)
    private String name;
    
    /**
     * Descripción detallada de la receta.
     */
    @Schema(description = "Descripción detallada de la receta, incluyendo características del yogurt resultante", 
            example = "Yogurt griego cremoso con alto contenido proteico, ideal para desayunos y postres. Textura espesa y sabor suave.",
            maxLength = 500)
    private String description;
    
    /**
     * Volumen de leche por defecto en litros.
     */
    @Schema(description = "Volumen de leche estándar en litros para esta receta", 
            example = "2.0", 
            required = true,
            minimum = "0.1")
    private Double defaultMilkVolume;
    
    /**
     * Cantidad de fermento por defecto en cucharadas.
     */
    @Schema(description = "Cantidad estándar de fermento (cultivo iniciador) en cucharadas", 
            example = "2.0", 
            required = true,
            minimum = "0.5")
    private Double defaultStarterAmount;
    
    /**
     * Temperatura de calentamiento en grados Celsius.
     */
    @Schema(description = "Temperatura objetivo de calentamiento en grados Celsius (°C). Típicamente entre 82-85°C para pasteurización.", 
            example = "85.0", 
            required = true,
            minimum = "30",
            maximum = "100")
    private Double heatingTemperature;
    
    /**
     * Duración del calentamiento en minutos.
     */
    @Schema(description = "Tiempo de mantenimiento de la temperatura de calentamiento en minutos", 
            example = "30", 
            required = true,
            minimum = "5")
    private Integer heatingDuration;
    
    /**
     * Temperatura de inoculación en grados Celsius.
     */
    @Schema(description = "Temperatura a la que se debe enfriar la leche antes de agregar el fermento (°C). Típicamente entre 40-45°C.", 
            example = "43.0", 
            required = true,
            minimum = "30",
            maximum = "50")
    private Double inoculationTemperature;
    
    /**
     * Temperatura de incubación en grados Celsius.
     */
    @Schema(description = "Temperatura de incubación para el desarrollo de los cultivos (°C). Debe mantenerse constante.", 
            example = "43.0", 
            required = true,
            minimum = "35",
            maximum = "50")
    private Double incubationTemperature;
    
    /**
     * Tiempo mínimo de incubación en horas.
     */
    @Schema(description = "Tiempo mínimo de incubación en horas. Menos tiempo produce yogurt más suave.", 
            example = "6", 
            required = true,
            minimum = "4")
    private Integer minIncubationTime;
    
    /**
     * Tiempo máximo de incubación en horas.
     */
    @Schema(description = "Tiempo máximo de incubación en horas. Más tiempo produce yogurt más ácido y firme.", 
            example = "12", 
            required = true,
            maximum = "24")
    private Integer maxIncubationTime;
    
    /**
     * Tiempo de refrigeración en horas.
     */
    @Schema(description = "Tiempo necesario de refrigeración en horas antes de que el yogurt esté listo para consumir", 
            example = "8", 
            required = true,
            minimum = "2")
    private Integer refrigerationTime;
    
    /**
     * Nivel de dificultad de la receta.
     */
    @Schema(description = "Nivel de dificultad para preparar esta receta de yogurt", 
            example = "BEGINNER",
            allowableValues = {"BEGINNER", "INTERMEDIATE", "ADVANCED"},
            required = true)
    private Recipe.DifficultyLevel difficulty;
    
    /**
     * Consejos y recomendaciones para la receta.
     */
    @Schema(description = "Consejos útiles, trucos y recomendaciones para obtener el mejor resultado con esta receta", 
            example = "Para un yogurt más cremoso, usar leche entera. Colar por 4 horas adicionales para obtener textura griega.",
            maxLength = 500)
    private String tips;
    
    /**
     * Lista de ingredientes de la receta.
     */
    @Schema(description = "Lista de ingredientes necesarios para preparar esta receta de yogurt")
    private List<IngredientDTO> ingredients;
}