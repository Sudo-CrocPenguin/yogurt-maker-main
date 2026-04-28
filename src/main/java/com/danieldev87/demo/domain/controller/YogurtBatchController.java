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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class YogurtBatchController {
    
    private final YogurtMakingService yogurtMakingService;
    
    @PostMapping
    public ResponseEntity<YogurtBatch> startNewBatch(@RequestBody BatchDTO.StartBatchRequest request) {
        YogurtBatch batch = yogurtMakingService.startNewBatch(
            request.getRecipeId(), 
            request.getCustomMilkVolume(), 
            request.getCustomStarterAmount()
        );
        return new ResponseEntity<>(batch, HttpStatus.CREATED);
    }
    
    @PostMapping("/{batchId}/heating")
    public ResponseEntity<YogurtBatch> startHeating(@PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startHeating(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @PostMapping("/{batchId}/inoculating")
    public ResponseEntity<YogurtBatch> startInoculating(@PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startInoculating(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @PostMapping("/{batchId}/incubation")
    public ResponseEntity<YogurtBatch> startIncubation(@PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startIncubation(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @PostMapping("/{batchId}/refrigeration")
    public ResponseEntity<YogurtBatch> startRefrigeration(@PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startRefrigeration(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @PostMapping("/{batchId}/complete")
    public ResponseEntity<YogurtBatch> completeBatch(@PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.completeBatch(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @PostMapping("/{batchId}/fail")
    public ResponseEntity<YogurtBatch> markAsFailed(
            @PathVariable Long batchId, 
            @RequestBody BatchDTO.FailRequest request) {
        YogurtBatch batch = yogurtMakingService.markAsFailed(batchId, request.getReason());
        return ResponseEntity.ok(batch);
    }
    
    @GetMapping
    public ResponseEntity<List<YogurtBatch>> getAllBatches(
            @RequestParam(required = false) YogurtBatch.BatchStatus status) {
        if (status != null) {
            return ResponseEntity.ok(yogurtMakingService.getBatchesByStatus(status));
        }
        return ResponseEntity.ok(yogurtMakingService.getAllBatches());
    }
    
    @GetMapping("/{batchId}")
    public ResponseEntity<YogurtBatch> getBatch(@PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.getBatch(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @PostMapping("/{batchId}/temperature")
    public ResponseEntity<Void> recordTemperature(
            @PathVariable Long batchId, 
            @RequestBody TemperatureRecordDTO request) {
        yogurtMakingService.recordTemperature(batchId, request.getTemperature(), request.getType());
        return ResponseEntity.ok().build();
    }
}
