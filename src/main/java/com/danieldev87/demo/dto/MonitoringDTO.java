package com.danieldev87.demo.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

public class MonitoringDTO {
    
    @Data
    @Builder
    public static class TemperatureSummary {
        private Double currentTemperature;
        private Double maximumTemperature;
        private Double minimumTemperature;
        private Double averageTemperature;
    }
    
    @Data
    @Builder
    public static class Dashboard {
        private Map<String, Long> batchCounts;
        private Long activeBatchesCount;
        private Integer completedToday;
    }
}
