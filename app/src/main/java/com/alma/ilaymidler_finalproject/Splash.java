package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    // זמן הספלאש במילישניות
    private static final long SPLASH_TIME = 3000; // 3 שניות

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // מעבר אוטומטי ל־MainActivity אחרי SPLASH_TIME
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish(); // סוגר את הספלאש כדי שלא יחזור בלחיצה על back
            }
        }, SPLASH_TIME);
    }
}
