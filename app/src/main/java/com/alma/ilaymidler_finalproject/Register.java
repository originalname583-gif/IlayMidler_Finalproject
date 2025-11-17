package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;



/// Activity for registering the user
/// This activity is used to register the user
/// It contains fields for the user to enter their information
/// It also contains a button to register the user
/// When the user is registered, they are redirected to the main activity
public class Register extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "RegisterActivity";



    private DatabaseService databaseService;

    private EditText etEmail, etPassword, etFName, etLName, etPhone;
    private Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        /// set the layout for the activity
        setContentView(R.layout.activity_register);

        databaseService=DatabaseService.getInstance();

        /// get the views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFName = findViewById(R.id.etFirstName);
        etLName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);


        /// set the click listener
        btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnRegister.getId()) {
            Log.d(TAG, "onClick: Register button clicked");

            /// get the input from the user
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String fName = etFName.getText().toString();
            String lName = etLName.getText().toString();
            String phone = etPhone.getText().toString();

          


            Log.d(TAG, "onClick: Registering user...");

            /// Register user
            registerUser(fName, lName, phone, email, password);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    /// Register the user
    private void registerUser(String fname, String lname, String phone, String email, String password) {
        Log.d(TAG, "registerUser: Registering user...");

        String uid = databaseService.generateUserId();

        /// create a new user object
        User user = new User(uid, fname, lname, phone,email, password);


        /// proceed to create the user
        createUserInDatabase(user, databaseService);

    }





private void createUserInDatabase(User user, DatabaseService databaseService) {
    databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
        @Override
        public void onCompleted(Void object) {
            Log.d(TAG, "createUserInDatabase: User created successfully");
            /// save the user to shared preferences

            Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");
            /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen

            /// clear the back stack (clear history) and start the MainActivity

        }

        @Override
        public void onFailed(Exception e) {
            Log.e(TAG, "createUserInDatabase: Failed to create user", e);
            /// show error message to user
            Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
            /// sign out the user if failed to register

        }
    });
}
}
