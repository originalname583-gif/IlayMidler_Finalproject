package com.alma.ilaymidler_finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;

import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private Button btnLogin;
    public static final String MyPREFERENCES = "MyPrefs";
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Pre-fill saved credentials
        etEmail.setText(sharedpreferences.getString("email", ""));
        etPassword.setText(sharedpreferences.getString("password", ""));

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnLogin.getId()) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        }
    }

    private void loginUser(String email, String password) {
        // Step 1: Use static LoginUser to get UID
        DatabaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                if(uid == null){
                    Toast.makeText(Login.this, "User not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save credentials and UID
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.putString("uid", uid);
                editor.apply();

                // Step 2: Fetch all users and find the current one
                DatabaseService.getInstance().getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
                    @Override
                    public void onCompleted(List<User> users) {
                        User currentUser = null;
                        for(User u : users){
                            if(u.getId().equals(uid)){
                                currentUser = u;
                                break;
                            }
                        }

                        if(currentUser == null){
                            Toast.makeText(Login.this, "User data not found!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Step 3: Redirect based on admin status
                        if(currentUser.isAdmin()){
                            startActivity(new Intent(Login.this, AdminPage.class));
                        } else {
                            startActivity(new Intent(Login.this, UserPage.class));
                        }

                        finish(); // Close login page
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(Login.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to fetch users", e);
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Login failed", e);
            }
        });
    }
}
