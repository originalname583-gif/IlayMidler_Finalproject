package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends BaseMenuActivity implements View.OnClickListener {

    private Button btnAbout, btnRegister, btnLogin;
    // כפתורים של המסך הראשי: אודות, הרשמה והתחברות.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מפעיל את onCreate של המחלקה האב.

        setContentView(R.layout.activity_main);
        // טוען את העיצוב של המסך הראשי.

        setupToolbar(R.id.topToolbar, "FieldTime");
        // מגדיר Toolbar עם שם האפליקציה.

        btnAbout = findViewById(R.id.btnAbout);
        // מחבר את כפתור About מה-XML לקוד.

        btnRegister = findViewById(R.id.btnRegister);
        // מחבר את כפתור Register מה-XML לקוד.

        btnLogin = findViewById(R.id.btnLogin);
        // מחבר את כפתור Login מה-XML לקוד.

        btnAbout.setOnClickListener(this);
        // מגדיר שהמסך הזה יטפל בלחיצה על כפתור About.

        btnRegister.setOnClickListener(this);
        // מגדיר שהמסך הזה יטפל בלחיצה על כפתור Register.

        btnLogin.setOnClickListener(this);
        // מגדיר שהמסך הזה יטפל בלחיצה על כפתור Login.
    }

    @Override
    public void onClick(View v) {
        // הפונקציה מופעלת כאשר המשתמש לוחץ על אחד הכפתורים במסך.

        if (v.getId() == R.id.btnAbout) {
            startActivity(new Intent(this, About.class));
            // אם נלחץ About, עוברים למסך אודות.
        } else if (v.getId() == R.id.btnRegister) {
            startActivity(new Intent(this, Register.class));
            // אם נלחץ Register, עוברים למסך הרשמה.
        } else if (v.getId() == R.id.btnLogin) {
            startActivity(new Intent(this, Login.class));
            // אם נלחץ Login, עוברים למסך התחברות.
        }
    }
}