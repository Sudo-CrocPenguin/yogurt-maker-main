package com.danieldev87.demo.domain.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.danieldev87.demo.domain.model.TemperatureLog;
import com.danieldev87.demo.domain.model.YogurtBatch;
import com.danieldev87.demo.domain.repository.TemperatureLogRepository;
import com.danieldev87.demo.domain.repository.YogurtBatchRepository;
import com.danieldev87.demo.domain.service.TemperatureControlService;
import com.danieldev87.demo.dto.MonitoringDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para el monitoreo de lotes de yogurt.
 * Proporciona endpoints para consultar el estado de los lotes,
 * temperaturas actuales e históricas, y un dashboard general.
 */
@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@Tag(name = "Monitoreo de Producción", 
     description = "Endpoints para supervisar el estado de los lotes de yogurt en producción, incluyendo temperaturas y métricas del dashboard")
public class MonitoringController {
    
    private final YogurtBatchRepository batchRepository;
    private final TemperatureLogRepository temperatureLogRepository;
    private final TemperatureControlService temperatureControlService;
    
    /**
     * Obtiene todos los lotes que están actualmente activos en el sistema.
     * Se consideran activos los lotes en estados: INCUBATING, HEATING, COOLING y REFRIGERATING.
     *
     * @return Lista de lotes activos con sus detalles completos
     */
    @Operation(
        summary = "Obtener lotes activos",
        description = "Devuelve la lista de todos los lotes de yogurt que se encuentran en producción activa. Incluye lotes en estados de incubación, calentamiento, enfriamiento y refrigeración."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de lotes activos obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = YogurtBatch.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al procesar la solicitud",
            content = @Content
        )
    })
    @GetMapping("/batches/active")
    public ResponseEntity<List<YogurtBatch>> getActiveBatches() {
        List<YogurtBatch> activeBatches = batchRepository.findByStatus(YogurtBatch.BatchStatus.INCUBATING);
        activeBatches.addAll(batchRepository.findByStatus(YogurtBatch.BatchStatus.HEATING));
        activeBatches.addAll(batchRepository.findByStatus(YogurtBatch.BatchStatus.COOLING));
        activeBatches.addAll(batchRepository.findByStatus(YogurtBatch.BatchStatus.REFRIGERATING));
        return ResponseEntity.ok(activeBatches);
    }
    
    /**
     * Obtiene un resumen de temperaturas para un lote específico.
     * Incluye temperatura actual, máxima, mínima y promedio.
     *
     * @param batchId Identificador único del lote de yogurt
     * @return Resumen de temperaturas con valores actuales e históricos
     */
    @Operation(
        summary = "Obtener resumen de temperaturas de un lote",
        description = "Devuelve un resumen consolidado de las temperaturas del lote especificado, incluyendo la temperatura actual, máxima, mínima y promedio durante la incubación. Útil para monitorear el progreso térmico del proceso."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Resumen de temperaturas obtenido correctamente",
            content = @Content(schema = @Schema(implementation = MonitoringDTO.TemperatureSummary.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Lote no encontrado con el ID proporcionado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID de lote inválido",
            content = @Content
        )
    })
    @GetMapping("/batches/{batchId}/temperature")
    public ResponseEntity<MonitoringDTO.TemperatureSummary> getBatchTemperatureSummary(
            @Parameter(description = "ID único del lote de yogurt", required = true, example = "1")
            @PathVariable Long batchId) {
        Double currentTemp = temperatureControlService.getCurrentTemperature(batchId);
        Double maxTemp = temperatureLogRepository.getMaxTemperatureByBatch(batchId);
        Double minTemp = temperatureLogRepository.getMinTemperatureByBatch(batchId);
        Double avgTemp = temperatureLogRepository.getAverageTemperatureByBatchAndType(
            batchId, TemperatureLog.LogType.INCUBATION);
        
        MonitoringDTO.TemperatureSummary summary = MonitoringDTO.TemperatureSummary.builder()
            .currentTemperature(currentTemp)
            .maximumTemperature(maxTemp)
            .minimumTemperature(minTemp)
            .averageTemperature(avgTemp)
            .build();
        
        return ResponseEntity.ok(summary);
    }
    
    /**
     * Obtiene el historial de registros de temperatura de un lote.
     * Permite filtrar por rango de fechas para análisis específicos.
     *
     * @param batchId Identificador único del lote
     * @param start Fecha de inicio del rango (opcional, formato ISO)
     * @param end Fecha de fin del rango (opcional, formato ISO)
     * @return Lista de registros de temperatura filtrados
     */
    @Operation(
        summary = "Obtener historial de temperaturas",
        description = "Recupera todos los registros de temperatura para un lote específico. Opcionalmente se puede filtrar por rango de fechas (start y end) usando formato ISO DateTime. Si no se especifican fechas, devuelve todos los registros del lote."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Registros de temperatura obtenidos exitosamente",
            content = @Content(schema = @Schema(implementation = TemperatureLog.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Lote no encontrado con el ID proporcionado",
            content = @Content
        )
    })
    @GetMapping("/batches/{batchId}/temperature-logs")
    public ResponseEntity<List<TemperatureLog>> getTemperatureLogs(
            @Parameter(description = "ID único del lote de yogurt", required = true, example = "1")
            @PathVariable Long batchId,
            @Parameter(description = "Fecha de inicio para filtrar registros (formato: yyyy-MM-ddTHH:mm:ss)", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "Fecha de fin para filtrar registros (formato: yyyy-MM-ddTHH:mm:ss)", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        if (start != null && end != null) {
            return ResponseEntity.ok(temperatureLogRepository.findByBatchAndTimeRange(batchId, start, end));
        }
        
        YogurtBatch batch = batchRepository.findById(batchId).orElseThrow();
        return ResponseEntity.ok(temperatureLogRepository.findByBatch(batch));
    }
    
    /**
     * Obtiene un dashboard con métricas generales del sistema.
     * Incluye conteos por estado, total de lotes activos y lotes completados hoy.
     *
     * @return Dashboard con métricas consolidadas del sistema
     */
    @Operation(
        summary = "Obtener dashboard general",
        description = "Devuelve un panel de control con métricas resumidas del sistema, incluyendo la cantidad de lotes por cada estado (PREPARING, HEATING, COOLING, INCUBATING, REFRIGERATING, COMPLETED, FAILED), el total de lotes activos y la cantidad de lotes completados en el día actual. Ideal para visualizar el estado general de la producción."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Dashboard obtenido correctamente",
            content = @Content(schema = @Schema(implementation = MonitoringDTO.Dashboard.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al generar el dashboard",
            content = @Content
        )
    })
    @GetMapping("/dashboard")
    public ResponseEntity<MonitoringDTO.Dashboard> getDashboard() {
        long preparingCount = batchRepository.countByStatus(YogurtBatch.BatchStatus.PREPARING);
        long heatingCount = batchRepository.countByStatus(YogurtBatch.BatchStatus.HEATING);
        long coolingCount = batchRepository.countByStatus(YogurtBatch.BatchStatus.COOLING);
        long incubatingCount = batchRepository.countByStatus(YogurtBatch.BatchStatus.INCUBATING);
        long refrigeratingCount = batchRepository.countByStatus(YogurtBatch.BatchStatus.REFRIGERATING);
        long completedCount = batchRepository.countByStatus(YogurtBatch.BatchStatus.COMPLETED);
        long failedCount = batchRepository.countByStatus(YogurtBatch.BatchStatus.FAILED);
        
        Map<String, Long> batchCounts = new HashMap<>();
        batchCounts.put("PREPARING", preparingCount);
        batchCounts.put("HEATING", heatingCount);
        batchCounts.put("COOLING", coolingCount);
        batchCounts.put("INCUBATING", incubatingCount);
        batchCounts.put("REFRIGERATING", refrigeratingCount);
        batchCounts.put("COMPLETED", completedCount);
        batchCounts.put("FAILED", failedCount);
        
        MonitoringDTO.Dashboard dashboard = MonitoringDTO.Dashboard.builder()
            .batchCounts(batchCounts)
            .activeBatchesCount(preparingCount + heatingCount + coolingCount + incubatingCount + refrigeratingCount)
            .completedToday(batchRepository.findByStatusAndDateRange(
                YogurtBatch.BatchStatus.COMPLETED, 
                LocalDateTime.now().withHour(0).withMinute(0), 
                LocalDateTime.now()).size())
            .build();
        
        return ResponseEntity.ok(dashboard);
    }
}