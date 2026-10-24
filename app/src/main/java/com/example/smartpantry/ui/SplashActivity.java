package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;

/** Welcome screen shown for 5 seconds, then opens the pantry list. */
public class SplashActivity extends AppCompatActivity {
    private static final long DELAY_MS = 5000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable openMain = () -> {
        startActivity(new Intent(this, PantryListActivity.class));
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(openMain, DELAY_MS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(openMain);
    }
}