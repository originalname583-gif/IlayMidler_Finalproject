package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;

public class Register extends AppCompatActivity {

    private EditText etFirstName, etLastName, etPhone, etGmail, etPassword;
    private Button btnRegister;

    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Connect views
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etGmail = findViewById(R.id.etGmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        databaseService = DatabaseService.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String fname = etFirstName.getText().toString().trim();
        String lname = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String gmail = etGmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(fname)) {
            etFirstName.setError("אנא הזן שם פרטי");
            return;
        }
        if (TextUtils.isEmpty(lname)) {
            etLastName.setError("אנא הזן שם משפחה");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("אנא הזן מספר טלפון");
            return;
        }
        if (TextUtils.isEmpty(gmail)) {
            etGmail.setError("אנא הזן Gmail");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("אנא הזן סיסמה");
            return;
        }

        // Check if Gmail already exists
        databaseService.checkIfEmailExists(gmail, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exists) {
                if (exists) {
                    Toast.makeText(Register.this, "המייל כבר קיים", Toast.LENGTH_SHORT).show();
                } else {
                    // Create new user
                    String uid = databaseService.generateUserId();
                    User user = new User(uid, fname, lname, phone, gmail, password);

                    databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            Toast.makeText(Register.this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                            // Go to MainActivity
                            Intent intent = new Intent(Register.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(Register.this, "שגיאה בהרשמה", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Register.this, "שגיאה בבדיקת המייל", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
