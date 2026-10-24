package com.example.smartpantry.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.data.PantryItem;
import com.example.smartpantry.data.RecipeIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class IngredientMatcherTest {

    private List<RecipeIngredient> omelette() {
        return Arrays.asList(
                new RecipeIngredient(1, "eggs", 2, "pcs"),
                new RecipeIngredient(1, "cheese", 50, "g"),
                new RecipeIngredient(1, "butter", 10, "g"));
    }

    private Map<String, Double> totals(PantryItem... items) {
        return IngredientMatcher.buildPantryTotals(new ArrayList<>(Arrays.asList(items)));
    }

    @Test public void pluralAndSingularNamesMatch() {
        assertEquals(IngredientMatcher.normalizeName("Tomatoes"), IngredientMatcher.normalizeName("tomato"));
        assertEquals("egg", IngredientMatcher.normalizeName(" Eggs "));
    }

    @Test public void allIngredientsPresentQualifies() {
        assertTrue(IngredientMatcher.canMake(omelette(), totals(
                new PantryItem("Egg", 6, "pcs", null),
                new PantryItem("Cheese", 200, "g", null),
                new PantryItem("Butter", 250, "g", null))));
    }

    @Test public void oneMissingIngredientIsExcluded() {
        assertFalse(IngredientMatcher.canMake(omelette(), totals(
                new PantryItem("Eggs", 6, "pcs", null),
                new PantryItem("Cheese", 200, "g", null))));
    }

    @Test public void insufficientQuantityIsExcluded() {
        assertFalse(IngredientMatcher.canMake(omelette(), totals(
                new PantryItem("Eggs", 1, "pcs", null),
                new PantryItem("Cheese", 200, "g", null),
                new PantryItem("Butter", 250, "g", null))));
    }

    @Test public void unitsAreConverted() {
        // volume (tbsp) must NOT satisfy a mass (g) requirement
        assertFalse(IngredientMatcher.canMake(omelette(), totals(
                new PantryItem("Eggs", 2, "pcs", null),
                new PantryItem("Cheese", 0.1, "kg", null),
                new PantryItem("Butter", 1, "tbsp", null))));
        assertTrue(IngredientMatcher.canMake(omelette(), totals(
                new PantryItem("Eggs", 2, "pcs", null),
                new PantryItem("Cheese", 0.1, "kg", null),
                new PantryItem("Butter", 0.01, "kg", null))));
    }

    @Test public void duplicatePantryRowsAreSummed() {
        assertTrue(IngredientMatcher.canMake(omelette(), totals(
                new PantryItem("Eggs", 1, "pcs", null),
                new PantryItem("Eggs", 1, "pcs", null),
                new PantryItem("Cheese", 50, "g", null),
                new PantryItem("Butter", 10, "g", null))));
    }
}
