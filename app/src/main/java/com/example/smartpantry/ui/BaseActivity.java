package com.example.smartpantry.ui;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.appbar.MaterialToolbar;
/** Shared bottom-navigation wiring. Each tab opens its Activity with an explicit Intent. */
public abstract class BaseActivity extends AppCompatActivity {

    protected void setupBottomNav(final int selectedId) {
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(selectedId);   // set before the listener so it doesn't fire
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) return true;
            Class<?> target;
            if (id == R.id.nav_pantry) target = PantryListActivity.class;
            else if (id == R.id.nav_suggested) target = SuggestedRecipesActivity.class;
            else target = SettingsActivity.class;
            Intent intent = new Intent(this, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            return true;


        });

    }
    protected void setupToolbarMenu() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            Class<?> target;
            if (id == R.id.menu_add) target = AddEditItemActivity.class;
            else if (id == R.id.menu_recipes) target = SuggestedRecipesActivity.class;
            else target = SettingsActivity.class;
            Intent intent = new Intent(this, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            return true;
        });
    }
}
