package com.danieldev87.demo.domain.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.danieldev87.demo.domain.model.YogurtBatch;
import com.danieldev87.demo.domain.service.YogurtMakingService;
import com.danieldev87.demo.dto.BatchDTO;
import com.danieldev87.demo.dto.TemperatureRecordDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión del ciclo de vida de lotes de yogurt.
 * Administra todo el flujo de producción desde la creación hasta la finalización.
 */
@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
@Tag(name = "Gestión de Lotes de Producción", 
     description = "Endpoints para controlar el ciclo completo de producción de yogurt: creación de lotes, transiciones de estado (calentamiento, inoculación, incubación, refrigeración) y registro de temperaturas")
public class YogurtBatchController {
    
    private final YogurtMakingService yogurtMakingService;
    
    /**
     * Inicia un nuevo lote de producción de yogurt.
     *
     * @param request Datos del nuevo lote (receta, volúmenes personalizados)
     * @return Lote creado con estado PREPARING
     */
    @Operation(
        summary = "Iniciar nuevo lote de producción",
        description = "Crea un nuevo lote de yogurt basado en una receta específica. Se pueden especificar volúmenes de leche y cantidad de fermento personalizados. Si no se especifican, se utilizan los valores por defecto de la receta. El lote se crea en estado PREPARING."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Lote creado exitosamente. Devuelve el lote completo con el código de lote generado automáticamente (formato: YB-{timestamp}).",
            content = @Content(schema = @Schema(implementation = YogurtBatch.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de solicitud inválidos. Verificar que el recipeId exista y los valores personalizados de volumen de leche y cantidad de fermento sean mayores a cero.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Receta no encontrada con el ID especificado en el campo recipeId",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<YogurtBatch> startNewBatch(
            @Parameter(description = "Datos para iniciar el lote: recipeId (obligatorio), customMilkVolume y customStarterAmount (opcionales)", required = true)
            @RequestBody BatchDTO.StartBatchRequest request) {
        YogurtBatch batch = yogurtMakingService.startNewBatch(
            request.getRecipeId(), 
            request.getCustomMilkVolume(), 
            request.getCustomStarterAmount()
        );
        return new ResponseEntity<>(batch, HttpStatus.CREATED);
    }
    
    /**
     * Inicia el proceso de calentamiento del lote.
     *
     * @param batchId ID del lote
     * @return Lote con estado actualizado a HEATING
     */
    @Operation(
        summary = "Iniciar calentamiento",
        description = "Transiciona el lote del estado PREPARING a HEATING e inicia el proceso automatizado de calentamiento gradual. El sistema simula el incremento de temperatura desde temperatura ambiente (~20°C) hasta alcanzar la temperatura objetivo definida en la receta. Durante este proceso se registran mediciones de temperatura cada 5 segundos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Proceso de calentamiento iniciado correctamente. El lote ahora está en estado HEATING.",
            content = @Content(schema = @Schema(implementation = YogurtBatch.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "El lote no está en estado PREPARING. Solo se puede iniciar calentamiento desde el estado PREPARING.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Lote no encontrado con el ID especificado",
            content = @Content
        )
    })
    @PostMapping("/{batchId}/heating")
    public ResponseEntity<YogurtBatch> startHeating(
            @Parameter(description = "ID único del lote de yogurt a calentar", required = true, example = "1")
            @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startHeating(batchId);
        return ResponseEntity.ok(batch);
    }
    
    /**
     * Inicia el proceso de inoculación del lote.
     *
     * @param batchId ID del lote
     * @return Lote con estado actualizado a INOCULATING
     */
    @Operation(
        summary = "Iniciar inoculación",
        description = "Transiciona el lote del estado COOLING a INOCULATING. En esta etapa se agrega el cultivo iniciador (fermento) a la leche que ya alcanzó la temperatura de inoculación adecuada. Este es un paso manual que debe realizarse cuando la leche esté a la temperatura correcta."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Proceso de inoculación registrado correctamente. El lote ahora está en estado INOCULATING.",
            content = @Content(schema = @Schema(implementation = YogurtBatch.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "El lote no está en estado COOLING. La inoculación solo puede realizarse después del enfriamiento.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Lote no encontrado con el ID especificado",
            content = @Content
        )
    })
    @PostMapping("/{batchId}/inoculating")
    public ResponseEntity<YogurtBatch> startInoculating(
            @Parameter(description = "ID único del lote de yogurt a inocular", required = true, example = "1")
            @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startInoculating(batchId);
        return ResponseEntity.ok(batch);
    }
    
    /**
     * Inicia el proceso de incubación.
     *
     * @param batchId ID del lote
     * @return Lote con estado actualizado a INCUBATING
     */
    @Operation(
        summary = "Iniciar incubación",
        description = "Transiciona el lote del estado INOCULATING a INCUBATING e inicia el control automatizado de temperatura durante el período de incubación. El sistema monitorea y registra la temperatura cada 5 minutos hasta completar el tiempo de incubación configurado en la receta. Se establecen las fechas de inicio y fin de incubación automáticamente."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Proceso de incubación iniciado correctamente. El lote ahora está en estado INCUBATING con control automático de temperatura.",
            content = @Content(schema = @Schema(implementation = YogurtBatch.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "El lote no está en estado INOCULATING. La incubación solo puede iniciarse después de la inoculación.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Lote no encontrado con el ID especificado",
            content = @Content
        )
    })
    @PostMapping("/{batchId}/incubation")
    public ResponseEntity<YogurtBatch> startIncubation(
            @Parameter(description = "ID único del lote de yogurt a incubar", required = true, example = "1")
            @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startIncubation(batchId);
        return ResponseEntity.ok(batch);
    }
    
    /**
     * Inicia el proceso de refrigeración del lote.
     *
     * @param batchId ID del lote
     * @return Lote con estado actualizado a REFRIGERATING
     */
    @Operation(
        summary = "Iniciar refrigeración",
        description = "Transiciona el lote del estado INCUBATING a REFRIGERATING. La incubación debe haber finalizado (tiempo de incubación cumplido) antes de poder refrigerar. La refrigeración detiene el proceso de fermentación y prepara el yogurt para su consumo final."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Proceso de refrigeración iniciado correctamente. El lote ahora está en estado REFRIGERATING.",
            content = @Content(schema = @Schema(implementation = YogurtBatch.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "El lote no está en estado INCUBATING o el tiempo de incubación aún no ha finalizado. Verificar la fecha incubationEndTime del lote.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Lote no encontrado con el ID especificado",
            content = @Content
        )
    })
    @PostMapping("/{batchId}/refrigeration")
    public ResponseEntity<YogurtBatch> startRefrigeration(
            @Parameter(description = "ID único del lote de yogurt a refrigerar", required = true, example = "1")
            @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startRefrigeration(batchId);
        return ResponseEntity.ok(batch);
    }
    
    /**
     * Marca un lote como completado exitosamente.
     *
     * @param batchId ID del lote
     * @return Lote con estado COMPLETED
     */
    @Operation(
        summary = "Completar lote de producción",
        description = "Marca el lote como COMPLETED exitosamente. El lote debe estar en estado REFRIGERATING y el tiempo de refrigeración especificado en la receta debe haberse cumplido. Un lote completado pasa a formar parte del historial de producción exitosa."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lote completado exitosamente. El yogurt está listo para consumo.",
            content = @Content(schema = @Schema(implementation = YogurtBatch.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "El lote no está en estado REFRIGERATING o el tiempo de refrigeración no se ha cumplido. Verificar el tiempo restante de refrigeración.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Lote no encontrado con el ID especificado",
            content = @Content
        )
    })
    @PostMapping("/{batchId}/complete")
    public ResponseEntity<YogurtBatch> completeBatch(
            @Parameter(description = "ID único del lote de yogurt a completar", required = true, example = "1")
            @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.completeBatch(batchId);
        return ResponseEntity.ok(batch);
    }
    
    /**
     * Marca un lote como fallido.
     *
     * @param batchId ID del lote
     * @param request Contiene el motivo del fallo
     * @return Lote con estado FAILED
     */
    @Operation(
        summary = "Marcar lote como fallido",
        description = "Marca el lote como FAILED debido a algún problema en el proceso. Se debe proporcionar un motivo que explique la razón del fallo. Esta acción se puede realizar en cualquier estado del lote y queda registrada en las notas del mismo para fines de trazabilidad."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lote marcado como fallido correctamente. El motivo queda registrado en las notas del lote.",
            content = @Content(schema = @Schema(implementation = YogurtBatch.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Solicitud inválida. El campo 'reason' con el motivo del fallo es obligatorio.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Lote no encontrado con el ID especificado",
            content = @Content
        )
    })
    @PostMapping("/{batchId}/fail")
    public ResponseEntity<YogurtBatch> markAsFailed(
            @Parameter(description = "ID único del lote de yogurt a marcar como fallido", required = true, example = "1")
            @PathVariable Long batchId, 
            @Parameter(description = "Objeto con el motivo del fallo (campo reason obligatorio)", required = true)
            @RequestBody BatchDTO.FailRequest request) {
        YogurtBatch batch = yogurtMakingService.markAsFailed(batchId, request.getReason());
        return ResponseEntity.ok(batch);
    }
    
    /**
     * Obtiene todos los lotes o filtra por estado.
     *
     * @param status Estado opcional para filtrar
     * @return Lista de lotes
     */
    @Operation(
        summary = "Listar lotes de producción",
        description = "Obtiene todos los lotes de producción del sistema. Opcionalmente se puede filtrar por estado (PREPARING, HEATING, COOLING, INOCULATING, INCUBATING, REFRIGERATING, COMPLETED, FAILED). Si no se especifica estado, devuelve todos los lotes sin filtrar."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de lotes obtenida correctamente. Puede estar vacía si no hay lotes que coincidan con el filtro.",
            content = @Content(schema = @Schema(implementation = YogurtBatch.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Valor de estado inválido. Debe ser uno de los estados definidos en el enum BatchStatus.",
            content = @Content
        )
    })
    @GetMapping
    public ResponseEntity<List<YogurtBatch>> getAllBatches(
            @Parameter(description = "Estado opcional para filtrar lotes (PREPARING, HEATING, COOLING, INOCULATING, INCUBATING, REFRIGERATING, COMPLETED, FAILED)", example = "INCUBATING")
            @RequestParam(required = false) YogurtBatch.BatchStatus status) {
        if (status != null) {
            return ResponseEntity.ok(yogurtMakingService.getBatchesByStatus(status));
        }
        return ResponseEntity.ok(yogurtMakingService.getAllBatches());
    }
    
    /**
     * Obtiene un lote específico por su ID.
     *
     * @param batchId ID del lote
     * @return Datos completos del lote
     */
    @Operation(
        summary = "Consultar lote por ID",
        description = "Recupera la información completa de un lote específico utilizando su identificador único. Incluye todos los datos del lote: receta asociada, estado actual, volúmenes, temperaturas, fechas de cada etapa y registros de temperatura."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lote encontrado y devuelto exitosamente con todos sus detalles.",
            content = @Content(schema = @Schema(implementation = YogurtBatch.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "No se encontró ningún lote con el ID especificado. Verificar que el ID sea correcto.",
            content = @Content
        )
    })
    @GetMapping("/{batchId}")
    public ResponseEntity<YogurtBatch> getBatch(
            @Parameter(description = "ID único del lote de yogurt a consultar", required = true, example = "1")
            @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.getBatch(batchId);
        return ResponseEntity.ok(batch);
    }
    
    /**
     * Registra una medición de temperatura manual para un lote.
     *
     * @param batchId ID del lote
     * @param request Datos de temperatura y tipo de registro
     */
    @Operation(
        summary = "Registrar temperatura manual",
        description = "Permite registrar manualmente una medición de temperatura para un lote específico. Útil para registrar temperaturas tomadas con termómetros externos o durante procesos manuales. Se debe especificar el valor de temperatura en °C y el tipo de registro (HEATING, COOLING, INCUBATION, REFRIGERATION, MANUAL)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Temperatura registrada exitosamente. El registro queda asociado al lote con la fecha y hora actual.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de temperatura inválidos. La temperatura debe ser un valor numérico válido y el tipo debe ser uno de los definidos en LogType.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Lote no encontrado con el ID especificado",
            content = @Content
        )
    })
    @PostMapping("/{batchId}/temperature")
    public ResponseEntity<Void> recordTemperature(
            @Parameter(description = "ID único del lote de yogurt", required = true, example = "1")
            @PathVariable Long batchId, 
            @Parameter(description = "Datos de temperatura a registrar: valor (temperature) y tipo de registro (type)", required = true)
            @RequestBody TemperatureRecordDTO request) {
        yogurtMakingService.recordTemperature(batchId, request.getTemperature(), request.getType());
        return ResponseEntity.ok().build();
    }
}