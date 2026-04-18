package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends BaseMenuActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupToolbar(R.id.topToolbar, "Login");

        mAuth = FirebaseAuth.getInstance();
        databaseService = DatabaseService.getInstance();

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful() || mAuth.getCurrentUser() == null) {
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String uid = mAuth.getCurrentUser().getUid();
                        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                            @Override
                            public void onCompleted(User user) {
                                if (user != null && user.isAdmin()) {
                                    startActivity(new Intent(Login.this, AdminPage.class));
                                } else {
                                    startActivity(new Intent(Login.this, UserPage.class));
                                }
                                finish();
                            }

                            @Override
                            public void onFailed(Exception e) {
                                Toast.makeText(Login.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
        });
    }
}