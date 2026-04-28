package com.danieldev87.demo.domain.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.danieldev87.demo.domain.model.Recipe;
import com.danieldev87.demo.domain.service.RecipeService;
import com.danieldev87.demo.dto.RecipeDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de recetas de yogurt.
 * Permite crear, consultar, actualizar y activar/desactivar recetas.
 */
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Gestión de Recetas", 
     description = "Endpoints para administrar las recetas de yogurt: creación, consulta, actualización y activación/desactivación de recetas disponibles en el sistema")
public class RecipeController {
    
    private final RecipeService recipeService;
    
    /**
     * Crea una nueva receta en el sistema.
     *
     * @param recipeDTO Datos de la receta a crear
     * @return Receta creada con su ID asignado
     */
    @Operation(
        summary = "Crear nueva receta",
        description = "Registra una nueva receta de yogurt en el sistema con todos sus parámetros e ingredientes. El nombre de la receta debe ser único. Si la creación es exitosa, la receta se marca automáticamente como activa."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Receta creada exitosamente. Devuelve el objeto Recipe completo con el ID generado.",
            content = @Content(schema = @Schema(implementation = Recipe.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de receta inválidos o nombre de receta duplicado. Verificar que todos los campos requeridos estén presentes y que el nombre no exista previamente.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al procesar la creación",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(
            @Parameter(description = "Datos completos de la receta a crear", required = true)
            @Valid @RequestBody RecipeDTO recipeDTO) {
        Recipe recipe = recipeService.createRecipe(recipeDTO);
        return new ResponseEntity<>(recipe, HttpStatus.CREATED);
    }
    
    /**
     * Actualiza una receta existente completamente.
     *
     * @param id ID de la receta a actualizar
     * @param recipeDTO Nuevos datos de la receta
     * @return Receta actualizada
     */
    @Operation(
        summary = "Actualizar receta existente",
        description = "Actualiza todos los campos de una receta existente identificada por su ID. Reemplaza completamente la información anterior, incluyendo la lista de ingredientes. La receta debe existir en el sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Receta actualizada correctamente con los nuevos valores",
            content = @Content(schema = @Schema(implementation = Recipe.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Receta no encontrada con el ID proporcionado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de actualización inválidos o inconsistentes",
            content = @Content
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @Parameter(description = "ID único de la receta a actualizar", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Nuevos datos completos de la receta", required = true)
            @Valid @RequestBody RecipeDTO recipeDTO) {
        Recipe recipe = recipeService.updateRecipe(id, recipeDTO);
        return ResponseEntity.ok(recipe);
    }
    
    /**
     * Obtiene una receta por su ID.
     *
     * @param id ID de la receta
     * @return Datos de la receta encontrada
     */
    @Operation(
        summary = "Consultar receta por ID",
        description = "Recupera la información completa de una receta específica utilizando su identificador único. Incluye todos los ingredientes asociados y parámetros de preparación."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Receta encontrada y devuelta exitosamente",
            content = @Content(schema = @Schema(implementation = Recipe.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "No se encontró ninguna receta con el ID especificado",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(
            @Parameter(description = "ID único de la receta a consultar", required = true, example = "1")
            @PathVariable Long id) {
        Recipe recipe = recipeService.getRecipe(id);
        return ResponseEntity.ok(recipe);
    }
    
    /**
     * Obtiene todas las recetas activas.
     *
     * @return Lista de recetas activas
     */
    @Operation(
        summary = "Listar recetas activas",
        description = "Devuelve todas las recetas que se encuentran actualmente activas en el sistema. Las recetas desactivadas no se incluyen en esta lista. Útil para mostrar las recetas disponibles para iniciar nuevos lotes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de recetas activas obtenida correctamente",
            content = @Content(schema = @Schema(implementation = Recipe.class))
        ),
        @ApiResponse(
            responseCode = "204", 
            description = "No hay recetas activas disponibles en el sistema",
            content = @Content
        )
    })
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllActiveRecipes());
    }
    
    /**
     * Busca recetas por palabra clave en nombre y descripción.
     *
     * @param keyword Palabra o frase para buscar
     * @return Lista de recetas que coinciden con la búsqueda
     */
    @Operation(
        summary = "Buscar recetas por palabra clave",
        description = "Realiza una búsqueda de recetas utilizando una palabra clave que se compara contra el nombre y la descripción de las recetas. La búsqueda no distingue mayúsculas/minúsculas y busca coincidencias parciales."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Búsqueda realizada exitosamente. Retorna las recetas que coinciden con el criterio.",
            content = @Content(schema = @Schema(implementation = Recipe.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Parámetro de búsqueda inválido o vacío",
            content = @Content
        )
    })
    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(
            @Parameter(description = "Palabra clave para buscar en nombres y descripciones de recetas", required = true, example = "natural")
            @RequestParam String keyword) {
        return ResponseEntity.ok(recipeService.searchRecipes(keyword));
    }
    
    /**
     * Desactiva una receta (borrado lógico).
     *
     * @param id ID de la receta a desactivar
     */
    @Operation(
        summary = "Desactivar receta",
        description = "Realiza un borrado lógico de la receta, marcándola como inactiva. Las recetas desactivadas no aparecen en las listas de recetas activas pero se conservan en el sistema para referencia histórica. Los lotes existentes que usan esta receta no se ven afectados."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Receta desactivada exitosamente",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Receta no encontrada con el ID especificado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "No se puede desactivar la receta (por ejemplo, tiene lotes en proceso)",
            content = @Content
        )
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateRecipe(
            @Parameter(description = "ID único de la receta a desactivar", required = true, example = "1")
            @PathVariable Long id) {
        recipeService.deactivateRecipe(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Reactiva una receta previamente desactivada.
     *
     * @param id ID de la receta a reactivar
     */
    @Operation(
        summary = "Activar receta",
        description = "Reactiva una receta que fue previamente desactivada, haciéndola nuevamente disponible para iniciar nuevos lotes de producción. La receta debe existir y estar en estado inactivo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Receta reactivada exitosamente",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Receta no encontrada con el ID especificado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "La receta ya se encuentra activa o no puede ser reactivada",
            content = @Content
        )
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateRecipe(
            @Parameter(description = "ID único de la receta a activar", required = true, example = "1")
            @PathVariable Long id) {
        recipeService.activateRecipe(id);
        return ResponseEntity.ok().build();
    }
}