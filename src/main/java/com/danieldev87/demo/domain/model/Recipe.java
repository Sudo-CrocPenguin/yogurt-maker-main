package com.danieldev87.demo.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Ingredient> ingredients = new ArrayList<>();
    
    @Column(nullable = false)
    private Double defaultMilkVolume; // litros
    
    @Column(nullable = false)
    private Double defaultStarterAmount; // cucharadas
    
    @Column(nullable = false)
    private Double heatingTemperature; // °C
    
    @Column(nullable = false)
    private Integer heatingDuration; // minutos a temperatura objetivo
    
    @Column(nullable = false)
    private Double inoculationTemperature; // °C
    
    @Column(nullable = false)
    private Double incubationTemperature; // °C
    
    @Column(nullable = false)
    private Integer minIncubationTime; // horas
    
    @Column(nullable = false)
    private Integer maxIncubationTime; // horas
    
    @Column(nullable = false)
    private Integer refrigerationTime; // horas
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty;
    
    private String tips;
    
    @Column(nullable = false)
    private Boolean active;
    
    public enum DifficultyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}

