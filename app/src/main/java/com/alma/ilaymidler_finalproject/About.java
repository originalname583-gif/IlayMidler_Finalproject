package com.alma.ilaymidler_finalproject;

import android.os.Bundle;
import android.widget.Button;

public class About extends BaseMenuActivity {

    private Button btnBack;
    // כפתור חזרה למסך הקודם.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מפעיל את onCreate של המחלקה האב.

        setContentView(R.layout.activity_about);
        // טוען את קובץ העיצוב של מסך About.

        setupToolbar(R.id.topToolbar, "About");
        // מגדיר את ה-Toolbar ומציג את הכותרת About.

        btnBack = findViewById(R.id.btnBack);
        // מחבר את הכפתור מה-XML לקוד.

        btnBack.setOnClickListener(v -> finish());
        // כאשר המשתמש לוחץ על הכפתור,
        // המסך נסגר וחוזרים למסך הקודם.
    }
}