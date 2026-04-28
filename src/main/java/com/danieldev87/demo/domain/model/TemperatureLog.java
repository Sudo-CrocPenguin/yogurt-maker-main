package com.danieldev87.demo.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "temperature_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemperatureLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private YogurtBatch batch;
    
    @Column(nullable = false)
    private Double temperature; // °C
    
    @Column(nullable = false)
    private LocalDateTime recordedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogType type;
    
    private String notes;
    
    public enum LogType {
        HEATING, COOLING, INCUBATION, REFRIGERATION, MANUAL
    }
}
