package com.danieldev87.demo.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.danieldev87.demo.domain.model.Ingredient;
import com.danieldev87.demo.domain.model.Recipe;
import com.danieldev87.demo.domain.repository.RecipeRepository;
import com.danieldev87.demo.dto.RecipeDTO;

import com.danieldev87.demo.exception.BusinessException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    
    @Transactional
    public Recipe createRecipe(RecipeDTO recipeDTO) {
        if (recipeRepository.findByName(recipeDTO.getName()).isPresent()) {
            throw new BusinessException("Recipe with name '" + recipeDTO.getName() + "' already exists");
        }
        
        Recipe recipe = Recipe.builder()
            .name(recipeDTO.getName())
            .description(recipeDTO.getDescription())
            .defaultMilkVolume(recipeDTO.getDefaultMilkVolume())
            .defaultStarterAmount(recipeDTO.getDefaultStarterAmount())
            .heatingTemperature(recipeDTO.getHeatingTemperature())
            .heatingDuration(recipeDTO.getHeatingDuration())
            .inoculationTemperature(recipeDTO.getInoculationTemperature())
            .incubationTemperature(recipeDTO.getIncubationTemperature())
            .minIncubationTime(recipeDTO.getMinIncubationTime())
            .maxIncubationTime(recipeDTO.getMaxIncubationTime())
            .refrigerationTime(recipeDTO.getRefrigerationTime())
            .difficulty(recipeDTO.getDifficulty())
            .tips(recipeDTO.getTips())
            .active(true)
            .build();
        
        // Agregar ingredientes
        if (recipeDTO.getIngredients() != null) {
            recipeDTO.getIngredients().forEach(ingredientDTO -> {
                Ingredient ingredient = Ingredient.builder()
                    .name(ingredientDTO.getName())
                    .quantity(ingredientDTO.getQuantity())
                    .unit(ingredientDTO.getUnit())
                    .notes(ingredientDTO.getNotes())
                    .optional(ingredientDTO.getOptional())
                    .recipe(recipe)
                    .build();
                recipe.getIngredients().add(ingredient);
            });
        }
        
        Recipe savedRecipe = recipeRepository.save(recipe);
        log.info("Recipe created: {}", savedRecipe.getName());
        
        return savedRecipe;
    }
    
    @Transactional
    public Recipe updateRecipe(Long id, RecipeDTO recipeDTO) {
        Recipe recipe = getRecipe(id);
        
        recipe.setName(recipeDTO.getName());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setDefaultMilkVolume(recipeDTO.getDefaultMilkVolume());
        recipe.setDefaultStarterAmount(recipeDTO.getDefaultStarterAmount());
        recipe.setHeatingTemperature(recipeDTO.getHeatingTemperature());
        recipe.setHeatingDuration(recipeDTO.getHeatingDuration());
        recipe.setInoculationTemperature(recipeDTO.getInoculationTemperature());
        recipe.setIncubationTemperature(recipeDTO.getIncubationTemperature());
        recipe.setMinIncubationTime(recipeDTO.getMinIncubationTime());
        recipe.setMaxIncubationTime(recipeDTO.getMaxIncubationTime());
        recipe.setRefrigerationTime(recipeDTO.getRefrigerationTime());
        recipe.setDifficulty(recipeDTO.getDifficulty());
        recipe.setTips(recipeDTO.getTips());
        
        // Actualizar ingredientes (simplificado - en producción se manejaría mejor)
        recipe.getIngredients().clear();
        if (recipeDTO.getIngredients() != null) {
            recipeDTO.getIngredients().forEach(ingredientDTO -> {
                Ingredient ingredient = Ingredient.builder()
                    .name(ingredientDTO.getName())
                    .quantity(ingredientDTO.getQuantity())
                    .unit(ingredientDTO.getUnit())
                    .notes(ingredientDTO.getNotes())
                    .optional(ingredientDTO.getOptional())
                    .recipe(recipe)
                    .build();
                recipe.getIngredients().add(ingredient);
            });
        }
        
        Recipe updatedRecipe = recipeRepository.save(recipe);
        log.info("Recipe updated: {}", updatedRecipe.getName());
        
        return updatedRecipe;
    }
    
    public Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Recipe not found with id: " + id));
    }
    
    public List<Recipe> getAllActiveRecipes() {
        return recipeRepository.findByActive(true);
    }
    
    public List<Recipe> searchRecipes(String keyword) {
        return recipeRepository.searchByKeyword(keyword);
    }
    
    @Transactional
    public void deactivateRecipe(Long id) {
        Recipe recipe = getRecipe(id);
        recipe.setActive(false);
        recipeRepository.save(recipe);
        log.info("Recipe deactivated: {}", recipe.getName());
    }
    
    @Transactional
    public void activateRecipe(Long id) {
        Recipe recipe = getRecipe(id);
        recipe.setActive(true);
        recipeRepository.save(recipe);
        log.info("Recipe activated: {}", recipe.getName());
    }
}
