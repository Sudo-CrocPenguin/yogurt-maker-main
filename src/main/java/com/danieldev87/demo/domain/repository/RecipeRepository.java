package com.danieldev87.demo.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.danieldev87.demo.domain.model.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    Optional<Recipe> findByName(String name);
    
    List<Recipe> findByActive(Boolean active);
    
    List<Recipe> findByDifficulty(Recipe.DifficultyLevel difficulty);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.difficulty = :difficulty")
    List<Recipe> findActiveByDifficulty(@Param("difficulty") Recipe.DifficultyLevel difficulty);
    
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Recipe> searchByKeyword(@Param("keyword") String keyword);
}
