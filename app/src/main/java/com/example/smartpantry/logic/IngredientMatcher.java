package com.example.smartpantry.logic;

import com.example.smartpantry.data.PantryItem;
import com.example.smartpantry.data.Recipe;
import com.example.smartpantry.data.RecipeIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Core business logic: the STRICT-MATCHING rule.
 *
 * A recipe qualifies only if EVERY ingredient is in the pantry in at least the required
 * quantity. Robustness: names are normalised (case, punctuation, plural/singular) and
 * quantities are converted to a base unit (g, ml or count) before comparing.
 * Pure Java (no Android classes) so it can be unit tested on the JVM.
 */
public final class IngredientMatcher {
    private static final double EPSILON = 1e-6;
    private static final Set<String> NOT_PLURAL = new HashSet<>(Arrays.asList(
            "hummus", "couscous", "asparagus", "molasses", "lemongrass"));
    private static final Map<String, String> UNIT_ALIASES = new HashMap<>();
    private static final Map<String, String> UNIT_CATEGORY = new HashMap<>();
    private static final Map<String, Double> UNIT_FACTOR = new HashMap<>();

    static {
        alias("g", "gram", "grams", "gr");
        alias("kg", "kilogram", "kilograms", "kgs");
        alias("ml", "millilitre", "millilitres", "milliliter", "milliliters");
        alias("l", "litre", "litres", "liter", "liters");
        alias("tsp", "teaspoon", "teaspoons");
        alias("tbsp", "tablespoon", "tablespoons");
        alias("cup", "cups");
        alias("pcs", "pc", "piece", "pieces", "unit", "units", "whole", "clove", "cloves",
                "slice", "slices", "tin", "tins", "can", "cans", "");
        unit("g", "mass", 1); unit("kg", "mass", 1000); unit("mg", "mass", 0.001);
        unit("oz", "mass", 28.35); unit("lb", "mass", 453.6);
        unit("ml", "volume", 1); unit("l", "volume", 1000); unit("tsp", "volume", 5);
        unit("tbsp", "volume", 15); unit("cup", "volume", 240);
        unit("pcs", "count", 1);
    }

    private IngredientMatcher() { }

    private static void alias(String canonical, String... others) {
        for (String o : others) UNIT_ALIASES.put(o, canonical);
    }

    private static void unit(String name, String category, double factor) {
        UNIT_CATEGORY.put(name, category);
        UNIT_FACTOR.put(name, factor);
    }

    /** "Tomatoes" -> "tomato", "  Olive  Oil " -> "olive oil". Only the last word is singularised. */
    public static String normalizeName(String raw) {
        if (raw == null) return "";
        String s = raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z ]", " ").replaceAll("\\s+", " ").trim();
        if (s.isEmpty()) return s;
        String[] words = s.split(" ");
        words[words.length - 1] = singularize(words[words.length - 1]);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(words[i]);
        }
        return sb.toString();
    }

    static String singularize(String w) {
        if (w.length() <= 3 || NOT_PLURAL.contains(w)) return w;
        if (w.endsWith("ies")) return w.substring(0, w.length() - 3) + "y";          // berries -> berry
        if (w.endsWith("oes")) return w.substring(0, w.length() - 2);               // tomatoes -> tomato
        if (w.endsWith("ches") || w.endsWith("shes") || w.endsWith("xes")) return w.substring(0, w.length() - 2);
        if (w.endsWith("ss") || w.endsWith("us")) return w;
        if (w.endsWith("s")) return w.substring(0, w.length() - 1);                 // eggs -> egg
        return w;
    }

    private static String cleanUnit(String unit) {
        String u = unit == null ? "" : unit.toLowerCase(Locale.ROOT).trim().replace(".", "");
        String canonical = UNIT_ALIASES.get(u);
        return canonical != null ? canonical : u;
    }

    /** "mass", "volume", "count", or the raw unit text if unknown (so it only matches itself). */
    public static String unitCategory(String unit) {
        String u = cleanUnit(unit);
        String c = UNIT_CATEGORY.get(u);
        return c != null ? c : u;
    }

    /** Converts a quantity to its base unit (g, ml, or count). */
    public static double toBase(double quantity, String unit) {
        Double f = UNIT_FACTOR.get(cleanUnit(unit));
        return quantity * (f != null ? f : 1.0);
    }

    private static String key(String name, String unit) {
        return normalizeName(name) + "|" + unitCategory(unit);
    }

    /** Sums pantry quantities per (normalised name, unit category), in base units. */
    public static Map<String, Double> buildPantryTotals(List<PantryItem> pantry) {
        Map<String, Double> totals = new HashMap<>();
        for (PantryItem item : pantry) {
            String k = key(item.name, item.unit);
            Double current = totals.get(k);
            totals.put(k, (current == null ? 0.0 : current) + toBase(item.quantity, item.unit));
        }
        return totals;
    }

    /** True only if every required ingredient is available in at least the required amount. */
    public static boolean canMake(List<RecipeIngredient> required, Map<String, Double> pantryTotals) {
        if (required == null || required.isEmpty()) return false;
        for (RecipeIngredient need : required) {
            Double have = pantryTotals.get(key(need.name, need.unit));
            if (have == null || have + EPSILON < toBase(need.quantity, need.unit)) {
                return false;   // one missing / insufficient ingredient disqualifies the recipe
            }
        }
        return true;
    }

    public static List<Recipe> findMakeable(List<PantryItem> pantry, List<Recipe> recipes,
                                            Map<Long, List<RecipeIngredient>> ingredientsByRecipe) {
        Map<String, Double> totals = buildPantryTotals(pantry);
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : recipes) {
            if (canMake(ingredientsByRecipe.get(r.id), totals)) result.add(r);
        }
        return result;
    }

    public static Map<Long, List<RecipeIngredient>> groupByRecipe(List<RecipeIngredient> all) {
        Map<Long, List<RecipeIngredient>> map = new HashMap<>();
        for (RecipeIngredient ri : all) {
            List<RecipeIngredient> list = map.get(ri.recipeId);
            if (list == null) { list = new ArrayList<>(); map.put(ri.recipeId, list); }
            list.add(ri);
        }
        return map;
    }

    /** Display helper: 2.0 -> "2", 0.5 -> "0.5". */
    public static String formatQuantity(double q) {
        return q == Math.floor(q) ? String.valueOf((long) q) : String.valueOf(q);
    }
}
