package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.AppDatabase;
import com.example.smartpantry.data.PantryItem;
import com.example.smartpantry.data.Recipe;
import com.example.smartpantry.data.RecipeIngredient;
import com.example.smartpantry.logic.IngredientMatcher;

import java.util.List;
import java.util.Map;

/** Runs the strict-matching rule against the current pantry and lists only makeable recipes. */
public class SuggestedRecipesActivity extends BaseActivity {
    private AppDatabase db;
    private RecipeAdapter adapter;
    private TextView tvEmpty, tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        setupBottomNav(R.id.nav_suggested);
        db = AppDatabase.getInstance(this);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvSummary = findViewById(R.id.tvSummary);

        RecyclerView rv = findViewById(R.id.recyclerRecipes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(recipe -> {
            Intent i = new Intent(this, RecipeDetailActivity.class);
            i.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id);
            startActivity(i);
        });
        rv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<PantryItem> pantry = db.pantryDao().getAll();
        List<Recipe> all = db.recipeDao().getAllRecipes();
        Map<Long, List<RecipeIngredient>> byRecipe =
                IngredientMatcher.groupByRecipe(db.recipeDao().getAllIngredients());

        List<Recipe> makeable = IngredientMatcher.findMakeable(pantry, all, byRecipe);
        adapter.setRecipes(makeable);

        if (makeable.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvSummary.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            tvSummary.setVisibility(View.VISIBLE);
            tvSummary.setText(makeable.size() + (makeable.size() == 1 ? " recipe" : " recipes")
                    + " you can cook right now");
        }
    }
}
