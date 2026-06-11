package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    private static final long SPLASH_TIME = 3000;
    // כמה זמן מסך הפתיחה יוצג במילישניות.
    // 3000 = 3 שניות.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מופעל כאשר המסך נוצר.

        setContentView(R.layout.activity_splash);
        // טוען את עיצוב מסך הפתיחה.

        startSplashTimer();
        // מפעיל את הטיימר של מסך הפתיחה.
    }

    private void startSplashTimer() {
        // הפונקציה מחכה מספר שניות ואז מעבירה למסך הראשי.

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // יוצר השהייה של 3 שניות.

            openMainActivity();
            // לאחר ההשהייה פותח את MainActivity.

        }, SPLASH_TIME);
    }

    private void openMainActivity() {
        // הפונקציה מעבירה את המשתמש למסך הראשי.

        Intent intent = new Intent(Splash.this, MainActivity.class);
        // יוצר Intent למסך הראשי.

        startActivity(intent);
        // פותח את MainActivity.

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
        // יוצר מעבר חלק (Fade In / Fade Out).

        finish();
        // סוגר את Splash כדי שלא יהיה אפשר לחזור אליו עם כפתור Back.
    }
}