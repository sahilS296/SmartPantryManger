package com.example.smartpantry;

import android.app.Application;

import com.example.smartpantry.data.AppDatabase;
import com.example.smartpantry.data.RecipeSeeder;

/** Application class: seeds the recipe table the first time the app runs. */
public class SmartPantryApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RecipeSeeder.seedIfEmpty(AppDatabase.getInstance(this));
    }
}
