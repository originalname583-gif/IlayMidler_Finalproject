package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText emailEditText;
    // שדה האימייל.

    private EditText passwordEditText;
    // שדה הסיסמה.

    private Button loginButton;
    // כפתור התחברות.

    private FirebaseAuth mAuth;
    // אחראי על התחברות Firebase.

    private boolean loginInProgress = false;
    // מונע לחיצות מרובות על כפתור Login.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מופעל כאשר המסך נפתח.

        setContentView(R.layout.activity_login);
        // טוען את קובץ העיצוב של המסך.

        mAuth = FirebaseAuth.getInstance();
        // מקבל גישה ל-Firebase Authentication.

        emailEditText = findViewById(R.id.editTextEmail);
        // מחבר את שדה האימייל מה-XML.

        passwordEditText = findViewById(R.id.editTextPassword);
        // מחבר את שדה הסיסמה מה-XML.

        loginButton = findViewById(R.id.buttonLogin);
        // מחבר את כפתור ההתחברות.

        loginButton.setOnClickListener(v -> loginUser());
        // כאשר המשתמש לוחץ על Login מפעילים התחברות.
    }

    private void loginUser() {
        // הפונקציה מבצעת התחברות למערכת.

        if (loginInProgress) {
            return;
            // אם כבר מתבצעת התחברות, לא מאפשרים עוד לחיצה.
        }

        String email = emailEditText.getText().toString().trim();
        // לוקח את האימייל שהמשתמש הכניס.

        String password = passwordEditText.getText().toString().trim();
        // לוקח את הסיסמה שהמשתמש הכניס.

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter email");
            return;
            // אם האימייל ריק מציג שגיאה.
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email");
            return;
            // אם האימייל לא תקין מציג שגיאה.
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Enter password");
            return;
            // אם הסיסמה ריקה מציג שגיאה.
        }

        loginInProgress = true;
        // מסמנים שהתחילה התחברות.

        loginButton.setEnabled(false);
        // מבטלים זמנית את הכפתור.

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        loginInProgress = false;
                        // מסמנים שהתחברות הסתיימה.

                        loginButton.setEnabled(true);
                        // מחזירים את הכפתור לפעולה.

                        Toast.makeText(
                                Login.this,
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Login failed",
                                Toast.LENGTH_LONG
                        ).show();
                        // מציגים את השגיאה האמיתית של Firebase.

                        return;
                    }

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    // מקבלים את המשתמש שהתחבר.

                    if (firebaseUser == null) {

                        loginInProgress = false;
                        loginButton.setEnabled(true);

                        Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    loadUserRole(firebaseUser.getUid());
                    // בודקים האם המשתמש מנהל או משתמש רגיל.
                });
    }

    private void loadUserRole(String uid) {
        // הפונקציה מביאה את המשתמש מה-Database.

        DatabaseService.getInstance().getUser(uid,
                new DatabaseService.DatabaseCallback<User>() {

                    @Override
                    public void onCompleted(User user) {

                        loginInProgress = false;
                        // מסמנים שסיימנו התחברות.

                        loginButton.setEnabled(true);
                        // מחזירים את הכפתור לפעולה.

                        if (user == null) {
                            Toast.makeText(Login.this,
                                    "User data not found",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(Login.this,
                                "Login successful",
                                Toast.LENGTH_SHORT).show();
                        // הודעת הצלחה.

                        Intent intent;

                        if (user.isAdmin()) {
                            // אם המשתמש מנהל.

                            intent = new Intent(Login.this, AdminPage.class);
                        } else {
                            // אם המשתמש רגיל.

                            intent = new Intent(Login.this, UserPage.class);
                        }

                        startActivity(intent);
                        // מעבר למסך המתאים.

                        finish();
                        // סוגר את מסך ההתחברות.
                    }

                    @Override
                    public void onFailed(Exception e) {

                        loginInProgress = false;
                        // מסמנים שסיימנו.

                        loginButton.setEnabled(true);
                        // מחזירים את הכפתור לפעולה.

                        Toast.makeText(Login.this,
                                "Failed loading user data",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}