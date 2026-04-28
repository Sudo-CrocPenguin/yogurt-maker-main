package com.danieldev87.demo.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.danieldev87.demo.domain.model.Recipe;
import com.danieldev87.demo.domain.model.TemperatureLog;
import com.danieldev87.demo.domain.model.YogurtBatch;
import com.danieldev87.demo.domain.repository.RecipeRepository;
import com.danieldev87.demo.domain.repository.TemperatureLogRepository;
import com.danieldev87.demo.domain.repository.YogurtBatchRepository;
import com.danieldev87.demo.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class YogurtMakingService {
    
    private final YogurtBatchRepository batchRepository;
    private final RecipeRepository recipeRepository;
    private final TemperatureLogRepository temperatureLogRepository;
    private final TemperatureControlService temperatureControlService;
    
    @Transactional
    public YogurtBatch startNewBatch(Long recipeId, Double customMilkVolume, Double customStarterAmount) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new BusinessException("Recipe not found with id: " + recipeId));
        
        Double milkVolume = customMilkVolume != null ? customMilkVolume : recipe.getDefaultMilkVolume();
        Double starterAmount = customStarterAmount != null ? customStarterAmount : recipe.getDefaultStarterAmount();
        
        YogurtBatch batch = YogurtBatch.builder()
            .recipe(recipe)
            .status(YogurtBatch.BatchStatus.PREPARING)
            .milkVolume(milkVolume)
            .starterAmount(starterAmount)
            .targetTemperature(recipe.getInoculationTemperature())
            .incubationTime(recipe.getMinIncubationTime())
            .startTime(LocalDateTime.now())
            .build();
        
        YogurtBatch savedBatch = batchRepository.save(batch);
        log.info("New yogurt batch started: {} with recipe: {}", savedBatch.getBatchCode(), recipe.getName());
        
        return savedBatch;
    }
    
    @Transactional
    public YogurtBatch startHeating(Long batchId) {
        YogurtBatch batch = getBatch(batchId);
        
        if (batch.getStatus() != YogurtBatch.BatchStatus.PREPARING) {
            throw new BusinessException("Batch must be in PREPARING status to start heating");
        }
        
        batch.setStatus(YogurtBatch.BatchStatus.HEATING);
        batch = batchRepository.save(batch);
        
        temperatureControlService.startHeatingProcess(batch);
        log.info("Started heating for batch: {}", batch.getBatchCode());
        
        return batch;
    }
    
    @Transactional
    public YogurtBatch startInoculating(Long batchId) {
        YogurtBatch batch = getBatch(batchId);
        
        if (batch.getStatus() != YogurtBatch.BatchStatus.COOLING) {
            throw new BusinessException("Batch must be in COOLING status to start inoculation");
        }
        
        batch.setStatus(YogurtBatch.BatchStatus.INOCULATING);
        return batchRepository.save(batch);
    }
    
    @Transactional
    public YogurtBatch startIncubation(Long batchId) {
        YogurtBatch batch = getBatch(batchId);
        
        if (batch.getStatus() != YogurtBatch.BatchStatus.INOCULATING) {
            throw new BusinessException("Batch must be in INOCULATING status to start incubation");
        }
        
        batch.setStatus(YogurtBatch.BatchStatus.INCUBATING);
        batch.setIncubationStartTime(LocalDateTime.now());
        batch.setIncubationEndTime(LocalDateTime.now().plusHours(batch.getIncubationTime()));
        
        batch = batchRepository.save(batch);
        temperatureControlService.startIncubationControl(batch);
        
        log.info("Started incubation for batch: {}", batch.getBatchCode());
        return batch;
    }
    
    @Transactional
    public YogurtBatch startRefrigeration(Long batchId) {
        YogurtBatch batch = getBatch(batchId);
        
        if (batch.getStatus() != YogurtBatch.BatchStatus.INCUBATING) {
            throw new BusinessException("Batch must be in INCUBATING status to start refrigeration");
        }
        
        if (LocalDateTime.now().isBefore(batch.getIncubationEndTime())) {
            throw new BusinessException("Incubation time not completed yet");
        }
        
        batch.setStatus(YogurtBatch.BatchStatus.REFRIGERATING);
        batch.setRefrigerationStartTime(LocalDateTime.now());
        
        batch = batchRepository.save(batch);
        log.info("Started refrigeration for batch: {}", batch.getBatchCode());
        
        return batch;
    }
    
    @Transactional
    public YogurtBatch completeBatch(Long batchId) {
        YogurtBatch batch = getBatch(batchId);
        
        if (batch.getStatus() != YogurtBatch.BatchStatus.REFRIGERATING) {
            throw new BusinessException("Batch must be in REFRIGERATING status to complete");
        }
        
        LocalDateTime expectedEndTime = batch.getRefrigerationStartTime()
            .plusHours(batch.getRecipe().getRefrigerationTime());
        
        if (LocalDateTime.now().isBefore(expectedEndTime)) {
            throw new BusinessException("Refrigeration time not completed yet");
        }
        
        batch.setStatus(YogurtBatch.BatchStatus.COMPLETED);
        batch = batchRepository.save(batch);
        
        log.info("Batch completed successfully: {}", batch.getBatchCode());
        return batch;
    }
    
    @Transactional
    public YogurtBatch markAsFailed(Long batchId, String reason) {
        YogurtBatch batch = getBatch(batchId);
        batch.setStatus(YogurtBatch.BatchStatus.FAILED);
        batch.setNotes("Failed: " + reason);
        
        batch = batchRepository.save(batch);
        log.warn("Batch marked as failed: {}, reason: {}", batch.getBatchCode(), reason);
        
        return batch;
    }
    
    public YogurtBatch getBatch(Long batchId) {
        return batchRepository.findById(batchId)
            .orElseThrow(() -> new BusinessException("Batch not found with id: " + batchId));
    }
    
    public List<YogurtBatch> getAllBatches() {
        return batchRepository.findAll();
    }
    
    public List<YogurtBatch> getBatchesByStatus(YogurtBatch.BatchStatus status) {
        return batchRepository.findByStatus(status);
    }
    
    public void recordTemperature(Long batchId, Double temperature, TemperatureLog.LogType type) {
        YogurtBatch batch = getBatch(batchId);
        
        TemperatureLog log = TemperatureLog.builder()
            .batch(batch)
            .temperature(temperature)
            .recordedAt(LocalDateTime.now())
            .type(type)
            .build();
        
        temperatureLogRepository.save(log);
    }
}
