package com.danieldev87.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDTO {
    private String name;
    private Double quantity;
    private String unit;
    private String notes;
        private Boolean optional;
}
