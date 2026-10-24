package com.example.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.Recipe;

import java.util.ArrayList;
import java.util.List;

/** Adapter for the Suggested Recipes list. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface Listener { void onRecipeClicked(Recipe recipe); }

    private final Listener listener;
    private List<Recipe> recipes = new ArrayList<>();

    public RecipeAdapter(Listener listener) { this.listener = listener; }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        final Recipe r = recipes.get(position);
        h.tvRecipeName.setText(r.name);
        h.itemView.setOnClickListener(v -> listener.onRecipeClicked(r));
    }

    @Override public int getItemCount() { return recipes.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvRecipeName;
        ViewHolder(View v) {
            super(v);
            tvRecipeName = v.findViewById(R.id.tvRecipeName);
        }
    }
}
