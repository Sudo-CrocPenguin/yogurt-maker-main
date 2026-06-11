package com.danieldev87.demo.domain.service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.danieldev87.demo.domain.model.TemperatureLog;
import com.danieldev87.demo.domain.model.YogurtBatch;
import com.danieldev87.demo.domain.model.YogurtBatch.BatchStatus;
import com.danieldev87.demo.domain.repository.TemperatureLogRepository;
import com.danieldev87.demo.domain.repository.YogurtBatchRepository;
import com.danieldev87.demo.dto.MonitoringDTO;
import com.danieldev87.demo.exception.BusinessException;
import com.danieldev87.demo.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private static final Set<BatchStatus> ACTIVE_STATUSES = EnumSet.of(
        BatchStatus.PREPARING,
        BatchStatus.HEATING,
        BatchStatus.COOLING,
        BatchStatus.INOCULATING,
        BatchStatus.INCUBATING,
        BatchStatus.REFRIGERATING
    );

    private final YogurtBatchRepository batchRepository;
    private final TemperatureLogRepository temperatureLogRepository;
    private final TemperatureControlService temperatureControlService;

    public List<YogurtBatch> getActiveBatches() {
        return batchRepository.findByStatusIn(ACTIVE_STATUSES);
    }

    public MonitoringDTO.TemperatureSummary getBatchTemperatureSummary(Long batchId) {
        ensureBatchExists(batchId);

        Double currentTemp = temperatureControlService.getCurrentTemperature(batchId);
        Double maxTemp = temperatureLogRepository.getMaxTemperatureByBatch(batchId);
        Double minTemp = temperatureLogRepository.getMinTemperatureByBatch(batchId);
        Double avgTemp = temperatureLogRepository.getAverageTemperatureByBatchAndType(
            batchId, TemperatureLog.LogType.INCUBATION);

        return MonitoringDTO.TemperatureSummary.builder()
            .currentTemperature(currentTemp)
            .maximumTemperature(maxTemp)
            .minimumTemperature(minTemp)
            .averageTemperature(avgTemp)
            .build();
    }

    public List<TemperatureLog> getTemperatureLogs(Long batchId, LocalDateTime start, LocalDateTime end) {
        YogurtBatch batch = getBatch(batchId);

        if (start == null && end == null) {
            return temperatureLogRepository.findByBatch(batch);
        }

        if (start == null || end == null) {
            throw new BusinessException("Both start and end must be provided to filter by date range");
        }

        if (start.isAfter(end)) {
            throw new BusinessException("Start date must be before end date");
        }

        return temperatureLogRepository.findByBatchAndTimeRange(batchId, start, end);
    }

    public MonitoringDTO.Dashboard getDashboard() {
        Map<String, Long> batchCounts = new LinkedHashMap<>();
        for (BatchStatus status : BatchStatus.values()) {
            batchCounts.put(status.name(), batchRepository.countByStatus(status));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();

        return MonitoringDTO.Dashboard.builder()
            .batchCounts(batchCounts)
            .activeBatchesCount(batchRepository.countByStatusIn(ACTIVE_STATUSES))
            .completedToday(batchRepository.countByStatusAndCreatedAtBetween(
                BatchStatus.COMPLETED,
                startOfDay,
                now
            ))
            .build();
    }

    private void ensureBatchExists(Long batchId) {
        if (!batchRepository.existsById(batchId)) {
            throw new ResourceNotFoundException("Batch not found with id: " + batchId);
        }
    }

    private YogurtBatch getBatch(Long batchId) {
        return batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));
    }
}
