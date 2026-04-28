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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class MonitoringController {
    
    private final YogurtBatchRepository batchRepository;
    private final TemperatureLogRepository temperatureLogRepository;
    private final TemperatureControlService temperatureControlService;
    
    @GetMapping("/batches/active")
    public ResponseEntity<List<YogurtBatch>> getActiveBatches() {
        List<YogurtBatch> activeBatches = batchRepository.findByStatus(YogurtBatch.BatchStatus.INCUBATING);
        activeBatches.addAll(batchRepository.findByStatus(YogurtBatch.BatchStatus.HEATING));
        activeBatches.addAll(batchRepository.findByStatus(YogurtBatch.BatchStatus.COOLING));
        activeBatches.addAll(batchRepository.findByStatus(YogurtBatch.BatchStatus.REFRIGERATING));
        return ResponseEntity.ok(activeBatches);
    }
    
    @GetMapping("/batches/{batchId}/temperature")
    public ResponseEntity<MonitoringDTO.TemperatureSummary> getBatchTemperatureSummary(@PathVariable Long batchId) {
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
    
    @GetMapping("/batches/{batchId}/temperature-logs")
    public ResponseEntity<List<TemperatureLog>> getTemperatureLogs(
            @PathVariable Long batchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        if (start != null && end != null) {
            return ResponseEntity.ok(temperatureLogRepository.findByBatchAndTimeRange(batchId, start, end));
        }
        
        YogurtBatch batch = batchRepository.findById(batchId).orElseThrow();
        return ResponseEntity.ok(temperatureLogRepository.findByBatch(batch));
    }
    
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
