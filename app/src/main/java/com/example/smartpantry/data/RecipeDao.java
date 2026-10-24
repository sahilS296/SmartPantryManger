package com.example.smartpantry.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeDao {
    @Insert long insertRecipe(Recipe recipe);
    @Insert void insertIngredients(List<RecipeIngredient> ingredients);
    @Query("SELECT COUNT(*) FROM recipes") int count();
    @Query("SELECT * FROM recipes ORDER BY name") List<Recipe> getAllRecipes();
    @Query("SELECT * FROM recipes WHERE id = :id") Recipe getRecipe(long id);
    @Query("SELECT * FROM recipe_ingredients") List<RecipeIngredient> getAllIngredients();
    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    List<RecipeIngredient> getIngredients(long recipeId);
}
