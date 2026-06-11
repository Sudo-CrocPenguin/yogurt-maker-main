package com.danieldev87.demo.domain.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.danieldev87.demo.domain.model.Ingredient;
import com.danieldev87.demo.domain.model.Recipe;
import com.danieldev87.demo.domain.repository.RecipeRepository;
import com.danieldev87.demo.dto.RecipeDTO;

import com.danieldev87.demo.exception.BusinessException;
import com.danieldev87.demo.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    
    @Transactional
    public Recipe createRecipe(RecipeDTO recipeDTO) {
        String recipeName = normalizeName(recipeDTO.getName());
        if (recipeRepository.findByNameIgnoreCase(recipeName).isPresent()) {
            throw new BusinessException("Recipe with name '" + recipeName + "' already exists", HttpStatus.CONFLICT);
        }
        
        Recipe recipe = Recipe.builder()
            .name(recipeName)
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
        
        replaceIngredients(recipe, recipeDTO);
        
        Recipe savedRecipe = recipeRepository.save(recipe);
        log.info("Recipe created: {}", savedRecipe.getName());
        
        return savedRecipe;
    }
    
    @Transactional
    public Recipe updateRecipe(Long id, RecipeDTO recipeDTO) {
        Recipe recipe = getRecipe(id);
        String recipeName = normalizeName(recipeDTO.getName());

        recipeRepository.findByNameIgnoreCase(recipeName)
            .filter(existingRecipe -> !existingRecipe.getId().equals(id))
            .ifPresent(existingRecipe -> {
                throw new BusinessException("Recipe with name '" + recipeName + "' already exists", HttpStatus.CONFLICT);
            });
        
        recipe.setName(recipeName);
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
        
        replaceIngredients(recipe, recipeDTO);
        
        Recipe updatedRecipe = recipeRepository.save(recipe);
        log.info("Recipe updated: {}", updatedRecipe.getName());
        
        return updatedRecipe;
    }
    
    public Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
    }
    
    public List<Recipe> getAllActiveRecipes() {
        return recipeRepository.findByActive(true);
    }
    
    public List<Recipe> searchRecipes(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException("Search keyword must not be blank");
        }
        return recipeRepository.searchByKeyword(keyword.trim());
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

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Recipe name must not be blank");
        }
        return name.trim();
    }

    private void replaceIngredients(Recipe recipe, RecipeDTO recipeDTO) {
        recipe.getIngredients().clear();
        if (recipeDTO.getIngredients() == null) {
            return;
        }

        recipeDTO.getIngredients().forEach(ingredientDTO -> {
            Ingredient ingredient = Ingredient.builder()
                .name(ingredientDTO.getName().trim())
                .quantity(ingredientDTO.getQuantity())
                .unit(ingredientDTO.getUnit().trim())
                .notes(ingredientDTO.getNotes())
                .optional(Boolean.TRUE.equals(ingredientDTO.getOptional()))
                .recipe(recipe)
                .build();
            recipe.getIngredients().add(ingredient);
        });
    }
}
