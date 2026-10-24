package com.example.smartpantry.data;

import java.util.ArrayList;
import java.util.List;

/** Pre-loads 20 recipes on first run. Ingredient format: "name|quantity|unit". */
public final class RecipeSeeder {
    private RecipeSeeder() { }

    public static void seedIfEmpty(AppDatabase db) {
        final RecipeDao dao = db.recipeDao();
        if (dao.count() > 0) return;
        db.runInTransaction(() -> {
            add(dao, "Scrambled Eggs on Toast", "1. Whisk the eggs with salt.\n2. Melt butter in a pan and scramble gently.\n3. Toast the bread and serve the eggs on top.",
                    "eggs|3|pcs", "bread|2|pcs", "butter|10|g", "salt|1|tsp");
            add(dao, "Tomato Pasta", "1. Boil the pasta until tender.\n2. Fry chopped onion and garlic in olive oil.\n3. Add chopped tomatoes and salt, simmer 10 minutes.\n4. Toss with the pasta.",
                    "pasta|200|g", "tomatoes|3|pcs", "onion|1|pcs", "garlic|2|pcs", "olive oil|1|tbsp", "salt|1|tsp");
            add(dao, "Cheese Omelette", "1. Beat the eggs.\n2. Melt butter in a pan and pour in the eggs.\n3. Add grated cheese, fold and serve.",
                    "eggs|2|pcs", "cheese|50|g", "butter|10|g");
            add(dao, "Pancakes", "1. Mix flour, sugar, egg and milk into a smooth batter.\n2. Fry ladles of batter until golden on both sides.",
                    "flour|150|g", "milk|200|ml", "eggs|1|pcs", "sugar|1|tbsp");
            add(dao, "Egg Fried Rice", "1. Cook and cool the rice.\n2. Fry onion in oil, push aside and scramble the eggs.\n3. Add rice and soy sauce, stir-fry 3 minutes.",
                    "rice|200|g", "eggs|2|pcs", "onion|1|pcs", "soy sauce|2|tbsp", "oil|1|tbsp");
            add(dao, "Cheese Toastie", "1. Butter the bread.\n2. Fill with cheese.\n3. Fry or grill until golden and melted.",
                    "bread|2|pcs", "cheese|40|g", "butter|10|g");
            add(dao, "Mashed Potatoes", "1. Peel and boil the potatoes until soft.\n2. Drain and mash with butter, milk and salt.",
                    "potatoes|500|g", "butter|30|g", "milk|50|ml", "salt|1|tsp");
            add(dao, "Chicken Stir Fry", "1. Slice the chicken, carrots and onion.\n2. Fry the chicken in oil until cooked.\n3. Add vegetables and soy sauce, stir-fry 5 minutes.",
                    "chicken|300|g", "carrots|2|pcs", "onion|1|pcs", "soy sauce|2|tbsp", "oil|1|tbsp");
            add(dao, "Vegetable Soup", "1. Chop carrots, potatoes and onion.\n2. Simmer in the stock for 25 minutes.\n3. Season with salt.",
                    "carrots|2|pcs", "potatoes|200|g", "onion|1|pcs", "vegetable stock|500|ml", "salt|1|tsp");
            add(dao, "Banana Oats", "1. Heat oats and milk for 5 minutes, stirring.\n2. Top with sliced banana.",
                    "oats|50|g", "milk|200|ml", "bananas|1|pcs");
            add(dao, "Garlic Bread", "1. Mash crushed garlic into the butter.\n2. Spread on the bread.\n3. Bake at 200C for 8 minutes.",
                    "bread|4|pcs", "butter|40|g", "garlic|2|pcs");
            add(dao, "Tuna Pasta", "1. Boil the pasta.\n2. Fry onion in olive oil, add the tuna.\n3. Toss with the drained pasta.",
                    "pasta|200|g", "tuna|1|pcs", "onion|1|pcs", "olive oil|1|tbsp");
            add(dao, "Rice and Beans", "1. Cook the rice.\n2. Fry onion, add beans and salt, heat through.\n3. Serve over the rice.",
                    "rice|200|g", "beans|400|g", "onion|1|pcs", "salt|1|tsp");
            add(dao, "Fried Egg Sandwich", "1. Fry the egg in butter.\n2. Place between two slices of bread.",
                    "bread|2|pcs", "eggs|1|pcs", "butter|10|g");
            add(dao, "Tomato Soup", "1. Soften the onion in butter.\n2. Add chopped tomatoes and stock, simmer 20 minutes.\n3. Blend until smooth.",
                    "tomatoes|4|pcs", "onion|1|pcs", "vegetable stock|300|ml", "butter|20|g");
            add(dao, "French Toast", "1. Whisk eggs, milk and sugar.\n2. Soak the bread slices.\n3. Fry in butter until golden.",
                    "bread|4|pcs", "eggs|2|pcs", "milk|100|ml", "sugar|1|tbsp", "butter|10|g");
            add(dao, "Peanut Butter Banana Toast", "1. Toast the bread.\n2. Spread peanut butter and top with sliced banana.",
                    "bread|2|pcs", "peanut butter|2|tbsp", "bananas|1|pcs");
            add(dao, "Chicken Rice Bowl", "1. Cook the rice.\n2. Fry chicken with onion, garlic and salt until done.\n3. Serve on the rice.",
                    "chicken|250|g", "rice|200|g", "onion|1|pcs", "garlic|2|pcs", "salt|1|tsp");
            add(dao, "Potato Omelette", "1. Fry sliced potatoes and onion in oil until soft.\n2. Pour over beaten eggs and salt.\n3. Cook until set, flipping once.",
                    "potatoes|300|g", "eggs|4|pcs", "onion|1|pcs", "oil|2|tbsp", "salt|1|tsp");
            add(dao, "Cheesy Pasta Bake", "1. Boil the pasta.\n2. Make a sauce from butter, flour and milk, stir in cheese.\n3. Mix with the pasta and bake at 190C for 20 minutes.",
                    "pasta|250|g", "cheese|100|g", "milk|200|ml", "butter|20|g", "flour|20|g");
        });
    }

    private static void add(RecipeDao dao, String name, String steps, String... ingredients) {
        long id = dao.insertRecipe(new Recipe(name, steps));
        List<RecipeIngredient> list = new ArrayList<>();
        for (String s : ingredients) {
            String[] p = s.split("\\|");
            list.add(new RecipeIngredient(id, p[0], Double.parseDouble(p[1]), p[2]));
        }
        dao.insertIngredients(list);
    }
}
