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

    private EditText etFullName, etEmail, etPhone, etCity, etPassword;
    private Button btnRegister;
    private TextView tvToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // חיבור אלמנטים מה־XML
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etCity = findViewById(R.id.etCity);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvToLogin = findViewById(R.id.tvToLogin);

        // לחיצה על כפתור הרשמה
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // לחיצה על טקסט "כבר יש לך חשבון? התחבר כאן"
        tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // מעבר למסך התחברות
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // פונקציה לבדיקה והרשמת משתמש
    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // בדיקות בסיסיות
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("אנא הזן שם מלא");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("אנא הזן אימייל");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("אנא הזן מספר טלפון");
            return;
        }
        if (TextUtils.isEmpty(city)) {
            etCity.setError("אנא הזן עיר מגורים");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("אנא הזן סיסמה");
            return;
        }

        // כאן אפשר להוסיף קוד שמריץ הרשמה לשרת או לשמירה מקומית
        Toast.makeText(this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();

        // מעבר למסך הראשי אחרי הרשמה
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish(); // סוגר את מסך ההרשמה
    }
}
