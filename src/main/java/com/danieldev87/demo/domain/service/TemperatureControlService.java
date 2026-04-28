package com.danieldev87.demo.domain.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.danieldev87.demo.domain.model.TemperatureLog;
import com.danieldev87.demo.domain.model.YogurtBatch;
import com.danieldev87.demo.domain.repository.TemperatureLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemperatureControlService {
    
    private final TemperatureLogRepository temperatureLogRepository;
    private final Random random = new Random();
    
    public void startHeatingProcess(YogurtBatch batch) {
        log.info("Starting heating process for batch: {} to target temperature: {}°C", 
            batch.getBatchCode(), batch.getRecipe().getHeatingTemperature());
        
        // Simular calentamiento gradual
        new Thread(() -> {
            try {
                double currentTemp = 20.0; // temperatura ambiente
                double targetTemp = batch.getRecipe().getHeatingTemperature();
                
                while (currentTemp < targetTemp) {
                    Thread.sleep(5000); // esperar 5 segundos entre mediciones
                    
                    // Simular incremento de temperatura con algunas variaciones
                    currentTemp += (targetTemp - currentTemp) * 0.1 + (random.nextDouble() - 0.5);
                    
                    if (currentTemp > targetTemp) {
                        currentTemp = targetTemp;
                    }
                    
                    recordTemperature(batch, currentTemp, TemperatureLog.LogType.HEATING);
                    log.debug("Heating temperature for batch {}: {}°C", batch.getBatchCode(), currentTemp);
                }
                
                // Una vez alcanzada la temperatura, mantener por el tiempo especificado
                log.info("Heating target temperature reached for batch: {}", batch.getBatchCode());
                maintainHeatingTemperature(batch);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Heating process interrupted for batch: {}", batch.getBatchCode());
            }
        }).start();
    }
    
    private void maintainHeatingTemperature(YogurtBatch batch) {
        new Thread(() -> {
            try {
                double targetTemp = batch.getRecipe().getHeatingTemperature();
                int durationMinutes = batch.getRecipe().getHeatingDuration();
                
                for (int i = 0; i < durationMinutes; i++) {
                    Thread.sleep(60000); // esperar 1 minuto
                    
                    // Simular pequeñas variaciones de temperatura
                    double temp = targetTemp + (random.nextDouble() - 0.5) * 2;
                    recordTemperature(batch, temp, TemperatureLog.LogType.HEATING);
                    
                    log.debug("Maintaining temperature for batch {}: {}°C", batch.getBatchCode(), temp);
                }
                
                // Después del calentamiento, iniciar enfriamiento
                startCoolingProcess(batch);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Heating maintenance interrupted for batch: {}", batch.getBatchCode());
            }
        }).start();
    }
    
    private void startCoolingProcess(YogurtBatch batch) {
        log.info("Starting cooling process for batch: {} to inoculation temperature: {}°C", 
            batch.getBatchCode(), batch.getRecipe().getInoculationTemperature());
        
        // Actualizar estado del batch a COOLING
        batch.setStatus(YogurtBatch.BatchStatus.COOLING);
        
        new Thread(() -> {
            try {
                double currentTemp = batch.getRecipe().getHeatingTemperature();
                double targetTemp = batch.getRecipe().getInoculationTemperature();
                
                while (currentTemp > targetTemp) {
                    Thread.sleep(10000); // esperar 10 segundos entre mediciones
                    
                    // Simular enfriamiento gradual
                    currentTemp -= (currentTemp - targetTemp) * 0.05 + (random.nextDouble() - 0.5);
                    
                    if (currentTemp < targetTemp) {
                        currentTemp = targetTemp;
                    }
                    
                    recordTemperature(batch, currentTemp, TemperatureLog.LogType.COOLING);
                    log.debug("Cooling temperature for batch {}: {}°C", batch.getBatchCode(), currentTemp);
                }
                
                log.info("Inoculation temperature reached for batch: {}", batch.getBatchCode());
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Cooling process interrupted for batch: {}", batch.getBatchCode());
            }
        }).start();
    }
    
    public void startIncubationControl(YogurtBatch batch) {
        log.info("Starting incubation control for batch: {} at {}°C", 
            batch.getBatchCode(), batch.getRecipe().getIncubationTemperature());
        
        new Thread(() -> {
            try {
                LocalDateTime endTime = batch.getIncubationEndTime();
                double targetTemp = batch.getRecipe().getIncubationTemperature();
                
                while (LocalDateTime.now().isBefore(endTime)) {
                    Thread.sleep(300000); // esperar 5 minutos entre mediciones
                    
                    // Simular control de temperatura durante la incubación
                    double temp = targetTemp + (random.nextDouble() - 0.5) * 0.8;
                    recordTemperature(batch, temp, TemperatureLog.LogType.INCUBATION);
                    
                    log.debug("Incubation temperature for batch {}: {}°C", batch.getBatchCode(), temp);
                }
                
                log.info("Incubation completed for batch: {}", batch.getBatchCode());
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Incubation control interrupted for batch: {}", batch.getBatchCode());
            }
        }).start();
    }
    
    private void recordTemperature(YogurtBatch batch, Double temperature, TemperatureLog.LogType type) {
        TemperatureLog log = TemperatureLog.builder()
            .batch(batch)
            .temperature(temperature)
            .recordedAt(LocalDateTime.now())
            .type(type)
            .build();
        
        temperatureLogRepository.save(log);
    }
    
    public Double getCurrentTemperature(Long batchId) {
        return temperatureLogRepository.findByBatchAndTypeOrderByRecordedAtDesc(
            batchId, TemperatureLog.LogType.INCUBATION)
            .stream()
            .findFirst()
            .map(TemperatureLog::getTemperature)
            .orElse(null);
    }
}
