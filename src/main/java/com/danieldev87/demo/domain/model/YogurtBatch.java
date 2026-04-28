package com.danieldev87.demo.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "yogurt_batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YogurtBatch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String batchCode;
    
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status;
    
    @Column(nullable = false)
    private Double milkVolume; // en litros
    
    @Column(nullable = false)
    private Double starterAmount; // en cucharadas
    
    @Column(nullable = false)
    private Double targetTemperature; // temperatura objetivo en °C
    
    @Column(nullable = false)
    private Integer incubationTime; // tiempo en horas
    
    private LocalDateTime startTime;
    private LocalDateTime incubationStartTime;
    private LocalDateTime incubationEndTime;
    private LocalDateTime refrigerationStartTime;
    
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TemperatureLog> temperatureLogs = new ArrayList<>();
    
    private String notes;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        batchCode = "YB-" + System.currentTimeMillis();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum BatchStatus {
        PREPARING, 
        HEATING, 
        COOLING, 
        INOCULATING, 
        INCUBATING, 
        REFRIGERATING, 
        COMPLETED, 
        FAILED
    }
}

