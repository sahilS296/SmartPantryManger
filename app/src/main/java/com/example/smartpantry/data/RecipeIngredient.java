package com.example.smartpantry.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** One ingredient line of a recipe (many-to-one with Recipe). */
@Entity(tableName = "recipe_ingredients",
        foreignKeys = @ForeignKey(entity = Recipe.class, parentColumns = "id",
                childColumns = "recipeId", onDelete = ForeignKey.CASCADE),
        indices = @Index("recipeId"))
public class RecipeIngredient {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long recipeId;
    @NonNull public String name;
    public double quantity;
    @NonNull public String unit;

    public RecipeIngredient(long recipeId, @NonNull String name, double quantity, @NonNull String unit) {
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
}
