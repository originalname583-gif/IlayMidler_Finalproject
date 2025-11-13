package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // חיבור אלמנטים מה־XML
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);


        // לחיצה על כפתור התחברות
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });


    }

    // פונקציה לבדיקה והתחברות משתמש
    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // בדיקות בסיסיות
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("אנא הזן אימייל");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("אנא הזן סיסמה");
            return;
        }

        // כאן תוכל להוסיף בדיקה מול שרת / DB
        Toast.makeText(this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();

        // מעבר למסך הראשי אחרי התחברות
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
