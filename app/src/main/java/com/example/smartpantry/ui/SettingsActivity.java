package com.example.smartpantry.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.smartpantry.R;
import com.example.smartpantry.data.AppDatabase;
import com.google.android.material.switchmaterial.SwitchMaterial;

/** Settings: expiring-soon highlight toggle (SharedPreferences) and clear-pantry action. */
public class SettingsActivity extends BaseActivity {
    public static final String PREFS_NAME = "smart_pantry_prefs";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupBottomNav(R.id.nav_settings);
        setupToolbarMenu();

        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SwitchMaterial sw = findViewById(R.id.switchExpiry);
        sw.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));
        sw.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, checked).apply());

        ((Button) findViewById(R.id.btnClearPantry)).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Clear pantry")
                        .setMessage("Delete ALL pantry items? This cannot be undone.")
                        .setPositiveButton("Clear", (d, w) -> {
                            AppDatabase.getInstance(this).pantryDao().deleteAll();
                            Toast.makeText(this, "Pantry cleared", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());
    }
}
