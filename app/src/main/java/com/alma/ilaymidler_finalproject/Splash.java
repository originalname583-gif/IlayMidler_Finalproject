package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    private static final long SPLASH_TIME = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Use main Looper for Handler (safe for Android 12+)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start MainActivity with fade animation
            Intent intent = new Intent(Splash.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Close SplashActivity
        }, SPLASH_TIME);
    }
}
