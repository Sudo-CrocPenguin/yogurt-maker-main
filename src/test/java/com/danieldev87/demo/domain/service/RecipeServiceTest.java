package com.danieldev87.demo.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;

import com.danieldev87.demo.domain.model.Ingredient;
import com.danieldev87.demo.domain.model.Recipe;
import com.danieldev87.demo.domain.repository.RecipeRepository;
import com.danieldev87.demo.dto.IngredientDTO;
import com.danieldev87.demo.dto.RecipeDTO;
import com.danieldev87.demo.exception.BusinessException;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void createRecipeDefaultsIngredientOptionalAndBackReference() {
        RecipeDTO dto = validRecipeDTO();
        dto.setName("  Yogurt Natural  ");
        dto.getIngredients().get(0).setOptional(null);

        when(recipeRepository.findByNameIgnoreCase("Yogurt Natural")).thenReturn(Optional.empty());
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recipe recipe = recipeService.createRecipe(dto);

        assertEquals("Yogurt Natural", recipe.getName());
        Ingredient ingredient = recipe.getIngredients().get(0);
        assertFalse(ingredient.getOptional());
        assertSame(recipe, ingredient.getRecipe());
        verify(recipeRepository).save(recipe);
    }

    @Test
    void updateRecipeRejectsDuplicateNameOwnedByAnotherRecipe() {
        Recipe existingRecipe = Recipe.builder().id(1L).name("Natural").build();
        Recipe duplicateRecipe = Recipe.builder().id(2L).name("Greek").build();
        RecipeDTO dto = validRecipeDTO();
        dto.setName("Greek");

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.findByNameIgnoreCase("Greek")).thenReturn(Optional.of(duplicateRecipe));

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> recipeService.updateRecipe(1L, dto)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    private RecipeDTO validRecipeDTO() {
        RecipeDTO dto = new RecipeDTO();
        dto.setName("Yogurt Natural");
        dto.setDescription("Receta base de yogurt natural.");
        dto.setDefaultMilkVolume(2.0);
        dto.setDefaultStarterAmount(2.0);
        dto.setHeatingTemperature(85.0);
        dto.setHeatingDuration(30);
        dto.setInoculationTemperature(43.0);
        dto.setIncubationTemperature(43.0);
        dto.setMinIncubationTime(6);
        dto.setMaxIncubationTime(10);
        dto.setRefrigerationTime(8);
        dto.setDifficulty(Recipe.DifficultyLevel.BEGINNER);
        dto.setTips("Usar leche entera para mejor textura.");

        IngredientDTO ingredient = new IngredientDTO();
        ingredient.setName("Leche entera");
        ingredient.setQuantity(2.0);
        ingredient.setUnit("litros");
        ingredient.setOptional(false);
        dto.setIngredients(List.of(ingredient));

        return dto;
    }
}
