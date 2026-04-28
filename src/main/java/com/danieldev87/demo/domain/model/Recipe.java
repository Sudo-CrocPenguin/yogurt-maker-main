package com.danieldev87.demo.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad principal que representa una receta de yogurt.
 * Contiene todos los parámetros de preparación: temperaturas, tiempos,
 * ingredientes y nivel de dificultad.
 */
@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Receta completa de yogurt con todos sus parámetros de preparación, ingredientes y configuraciones de temperatura y tiempo")
public class Recipe {
    
    /**
     * Identificador único autogenerado de la receta.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único autogenerado de la receta en la base de datos", 
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    /**
     * Nombre único de la receta. No puede haber dos recetas con el mismo nombre.
     */
    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre único de la receta de yogurt. No puede repetirse en el sistema.", 
            example = "Yogurt Griego Natural",
            required = true,
            minLength = 3,
            maxLength = 100)
    private String name;
    
    /**
     * Descripción detallada de la receta y sus características.
     */
    @Schema(description = "Descripción detallada de la receta, resultado esperado y características del yogurt", 
            example = "Yogurt griego cremoso de textura espesa, ideal para desayunos. Alto contenido proteico y sabor suave.",
            maxLength = 500)
    private String description;
    
    /**
     * Lista de ingredientes necesarios para la receta.
     * Relación uno a muchos con cascada completa.
     */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @Builder.Default
    @Schema(description = "Lista de ingredientes necesarios para preparar esta receta de yogurt")
    private List<Ingredient> ingredients = new ArrayList<>();
    
    /**
     * Volumen de leche por defecto en litros.
     */
    @Column(nullable = false)
    @Schema(description = "Volumen estándar de leche en litros para esta receta", 
            example = "2.0",
            required = true,
            minimum = "0.1")
    private Double defaultMilkVolume;
    
    /**
     * Cantidad de fermento por defecto en cucharadas.
     */
    @Column(nullable = false)
    @Schema(description = "Cantidad estándar de fermento (cultivo iniciador) en cucharadas", 
            example = "2.0",
            required = true,
            minimum = "0.5")
    private Double defaultStarterAmount;
    
    /**
     * Temperatura objetivo de calentamiento en grados Celsius.
     * Típicamente entre 82-85°C para pasteurización.
     */
    @Column(nullable = false)
    @Schema(description = "Temperatura objetivo para la fase de calentamiento/pasteurización en °C", 
            example = "85.0",
            required = true,
            minimum = "30",
            maximum = "100")
    private Double heatingTemperature;
    
    /**
     * Duración del calentamiento en minutos una vez alcanzada la temperatura objetivo.
     */
    @Column(nullable = false)
    @Schema(description = "Tiempo en minutos que se debe mantener la temperatura de calentamiento", 
            example = "30",
            required = true,
            minimum = "5")
    private Integer heatingDuration;
    
    /**
     * Temperatura a la que se debe enfriar la leche antes de inocular.
     * Típicamente entre 40-45°C.
     */
    @Column(nullable = false)
    @Schema(description = "Temperatura de inoculación en °C a la que se agrega el fermento", 
            example = "43.0",
            required = true,
            minimum = "30",
            maximum = "50")
    private Double inoculationTemperature;
    
    /**
     * Temperatura constante para la fase de incubación.
     */
    @Column(nullable = false)
    @Schema(description = "Temperatura constante para la fase de incubación en °C", 
            example = "43.0",
            required = true,
            minimum = "35",
            maximum = "50")
    private Double incubationTemperature;
    
    /**
     * Tiempo mínimo de incubación en horas.
     */
    @Column(nullable = false)
    @Schema(description = "Tiempo mínimo de incubación en horas (resulta en yogurt más suave)", 
            example = "6",
            required = true,
            minimum = "4")
    private Integer minIncubationTime;
    
    /**
     * Tiempo máximo de incubación en horas.
     */
    @Column(nullable = false)
    @Schema(description = "Tiempo máximo de incubación en horas (resulta en yogurt más ácido y firme)", 
            example = "12",
            required = true,
            maximum = "24")
    private Integer maxIncubationTime;
    
    /**
     * Tiempo de refrigeración necesario en horas antes del consumo.
     */
    @Column(nullable = false)
    @Schema(description = "Tiempo de refrigeración necesario en horas antes de que el yogurt esté listo para consumir", 
            example = "8",
            required = true,
            minimum = "2")
    private Integer refrigerationTime;
    
    /**
     * Nivel de dificultad de la receta.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Nivel de dificultad para preparar esta receta", 
            example = "BEGINNER",
            allowableValues = {"BEGINNER", "INTERMEDIATE", "ADVANCED"},
            required = true)
    private DifficultyLevel difficulty;
    
    /**
     * Consejos y trucos para obtener mejores resultados.
     */
    @Schema(description = "Consejos, trucos y recomendaciones para obtener el mejor resultado con esta receta", 
            example = "Para un yogurt más cremoso, use leche entera. Puede colar por 4 horas adicionales para textura estilo griego.",
            maxLength = 500)
    private String tips;
    
    /**
     * Estado de la receta: activa (true) o desactivada (false).
     * Las recetas desactivadas no aparecen para iniciar nuevos lotes.
     */
    @Column(nullable = false)
    @Schema(description = "Indica si la receta está activa y disponible para usar en nuevos lotes", 
            example = "true",
            defaultValue = "true")
    private Boolean active;
    
    /**
     * Enumeración de niveles de dificultad disponibles para las recetas.
     */
    @Schema(description = "Niveles de dificultad para las recetas de yogurt")
    public enum DifficultyLevel {
        @Schema(description = "Nivel principiante: apto para personas sin experiencia previa")
        BEGINNER, 
        @Schema(description = "Nivel intermedio: requiere cierto conocimiento del proceso")
        INTERMEDIATE, 
        @Schema(description = "Nivel avanzado: para usuarios experimentados en la elaboración de yogurt")
        ADVANCED
    }
}