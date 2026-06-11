package com.danieldev87.demo.dto;

import java.util.List;

import com.danieldev87.demo.domain.model.Recipe;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "El nombre de la receta es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;
    
    /**
     * Descripción detallada de la receta.
     */
    @Schema(description = "Descripción detallada de la receta, incluyendo características del yogurt resultante", 
            example = "Yogurt griego cremoso con alto contenido proteico, ideal para desayunos y postres. Textura espesa y sabor suave.",
            maxLength = 500)
    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String description;
    
    /**
     * Volumen de leche por defecto en litros.
     */
    @Schema(description = "Volumen de leche estándar en litros para esta receta", 
            example = "2.0", 
            required = true,
            minimum = "0.1")
    @NotNull(message = "El volumen de leche por defecto es obligatorio")
    @DecimalMin(value = "0.1", message = "El volumen de leche debe ser mayor o igual a 0.1 litros")
    private Double defaultMilkVolume;
    
    /**
     * Cantidad de fermento por defecto en cucharadas.
     */
    @Schema(description = "Cantidad estándar de fermento (cultivo iniciador) en cucharadas", 
            example = "2.0", 
            required = true,
            minimum = "0.5")
    @NotNull(message = "La cantidad de fermento por defecto es obligatoria")
    @DecimalMin(value = "0.5", message = "La cantidad de fermento debe ser mayor o igual a 0.5 cucharadas")
    private Double defaultStarterAmount;
    
    /**
     * Temperatura de calentamiento en grados Celsius.
     */
    @Schema(description = "Temperatura objetivo de calentamiento en grados Celsius (°C). Típicamente entre 82-85°C para pasteurización.", 
            example = "85.0", 
            required = true,
            minimum = "30",
            maximum = "100")
    @NotNull(message = "La temperatura de calentamiento es obligatoria")
    @DecimalMin(value = "30.0", message = "La temperatura de calentamiento debe ser al menos 30°C")
    @DecimalMax(value = "100.0", message = "La temperatura de calentamiento no puede superar 100°C")
    private Double heatingTemperature;
    
    /**
     * Duración del calentamiento en minutos.
     */
    @Schema(description = "Tiempo de mantenimiento de la temperatura de calentamiento en minutos", 
            example = "30", 
            required = true,
            minimum = "5")
    @NotNull(message = "La duración del calentamiento es obligatoria")
    @Min(value = 5, message = "La duración del calentamiento debe ser de al menos 5 minutos")
    private Integer heatingDuration;
    
    /**
     * Temperatura de inoculación en grados Celsius.
     */
    @Schema(description = "Temperatura a la que se debe enfriar la leche antes de agregar el fermento (°C). Típicamente entre 40-45°C.", 
            example = "43.0", 
            required = true,
            minimum = "30",
            maximum = "50")
    @NotNull(message = "La temperatura de inoculación es obligatoria")
    @DecimalMin(value = "30.0", message = "La temperatura de inoculación debe ser al menos 30°C")
    @DecimalMax(value = "50.0", message = "La temperatura de inoculación no puede superar 50°C")
    private Double inoculationTemperature;
    
    /**
     * Temperatura de incubación en grados Celsius.
     */
    @Schema(description = "Temperatura de incubación para el desarrollo de los cultivos (°C). Debe mantenerse constante.", 
            example = "43.0", 
            required = true,
            minimum = "35",
            maximum = "50")
    @NotNull(message = "La temperatura de incubación es obligatoria")
    @DecimalMin(value = "35.0", message = "La temperatura de incubación debe ser al menos 35°C")
    @DecimalMax(value = "50.0", message = "La temperatura de incubación no puede superar 50°C")
    private Double incubationTemperature;
    
    /**
     * Tiempo mínimo de incubación en horas.
     */
    @Schema(description = "Tiempo mínimo de incubación en horas. Menos tiempo produce yogurt más suave.", 
            example = "6", 
            required = true,
            minimum = "4")
    @NotNull(message = "El tiempo mínimo de incubación es obligatorio")
    @Min(value = 4, message = "El tiempo mínimo de incubación debe ser al menos 4 horas")
    private Integer minIncubationTime;
    
    /**
     * Tiempo máximo de incubación en horas.
     */
    @Schema(description = "Tiempo máximo de incubación en horas. Más tiempo produce yogurt más ácido y firme.", 
            example = "12", 
            required = true,
            maximum = "24")
    @NotNull(message = "El tiempo máximo de incubación es obligatorio")
    @Min(value = 4, message = "El tiempo máximo de incubación debe ser al menos 4 horas")
    @Max(value = 24, message = "El tiempo máximo de incubación no puede superar 24 horas")
    private Integer maxIncubationTime;
    
    /**
     * Tiempo de refrigeración en horas.
     */
    @Schema(description = "Tiempo necesario de refrigeración en horas antes de que el yogurt esté listo para consumir", 
            example = "8", 
            required = true,
            minimum = "2")
    @NotNull(message = "El tiempo de refrigeración es obligatorio")
    @Min(value = 2, message = "El tiempo de refrigeración debe ser al menos 2 horas")
    private Integer refrigerationTime;
    
    /**
     * Nivel de dificultad de la receta.
     */
    @Schema(description = "Nivel de dificultad para preparar esta receta de yogurt", 
            example = "BEGINNER",
            allowableValues = {"BEGINNER", "INTERMEDIATE", "ADVANCED"},
            required = true)
    @NotNull(message = "La dificultad es obligatoria")
    private Recipe.DifficultyLevel difficulty;
    
    /**
     * Consejos y recomendaciones para la receta.
     */
    @Schema(description = "Consejos útiles, trucos y recomendaciones para obtener el mejor resultado con esta receta", 
            example = "Para un yogurt más cremoso, usar leche entera. Colar por 4 horas adicionales para obtener textura griega.",
            maxLength = 500)
    @Size(max = 500, message = "Los consejos no pueden superar 500 caracteres")
    private String tips;
    
    /**
     * Lista de ingredientes de la receta.
     */
    @Schema(description = "Lista de ingredientes necesarios para preparar esta receta de yogurt")
    @Valid
    private List<IngredientDTO> ingredients;

    @AssertTrue(message = "El tiempo mínimo de incubación no puede ser mayor que el tiempo máximo")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isIncubationRangeValid() {
        if (minIncubationTime == null || maxIncubationTime == null) {
            return true;
        }
        return minIncubationTime <= maxIncubationTime;
    }
}
