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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends BaseMenuActivity implements View.OnClickListener {

    private static final String TAG = "Register";
    public static final String MyPREFERENCES = "MyPrefs";

    private EditText etFname, etLname, etMail, etPhone, etPassword;
    private Button btnSubmit;
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        setupToolbar(R.id.topToolbar, "Register");

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etFname = findViewById(R.id.etFirstName);
        etLname = findViewById(R.id.etLastName);
        etMail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnSubmit = findViewById(R.id.btnRegister);

        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String fName = etFname.getText().toString().trim();
        String lName = etLname.getText().toString().trim();
        String email = etMail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        registerUser(fName, lName, phone, email, password);
    }

    private void registerUser(String fname, String lname, String phone, String email, String password) {


            User user = new User("nhh", fname, lname, email, phone, password, false);
                    createUserInDatabase(user);

    }

    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                user.setId(uid);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("email", user.getEmail());

                editor.putString("password", user.getPassword());
                editor.apply();

                startActivity(new Intent(Register.this, UserPage.class));
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}