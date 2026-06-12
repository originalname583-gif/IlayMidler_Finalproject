package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.alma.ilaymidler_finalproject.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText emailEditText;
    // תיבת הכנסת אימייל.

    private EditText passwordEditText;
    // תיבת הכנסת סיסמה.

    private Button loginButton;
    // כפתור התחברות.

    private FirebaseAuth mAuth;
    // אחראי על ההתחברות דרך Firebase Authentication.

    private DatabaseService databaseService;
    // אחראי על קריאה וכתיבה למסד הנתונים.

    private boolean loginInProgress = false;
    // מונע לחיצות מרובות על כפתור Login.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // הפונקציה הראשונה שרצה כשהמסך נפתח.

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        // מציג את עיצוב המסך.

        mAuth = FirebaseAuth.getInstance();
        // יוצר חיבור ל-Firebase Authentication.

        databaseService = DatabaseService.getInstance();
        // מקבל גישה לשירות מסד הנתונים.

        emailEditText = findViewById(R.id.editTextEmail);
        // מחבר את שדה האימייל מה-XML.

        passwordEditText = findViewById(R.id.editTextPassword);
        // מחבר את שדה הסיסמה מה-XML.

        loginButton = findViewById(R.id.buttonLogin);
        // מחבר את כפתור ההתחברות.

        loginButton.setOnClickListener(v -> loginUser());
        // כאשר המשתמש לוחץ על Login מפעילים את loginUser().
    }

    private void loginUser() {
        // הפונקציה מבצעת בדיקות לפני התחברות.

        if (loginInProgress) {
            return;
            // אם כבר מתבצעת התחברות לא נותנים להתחיל עוד אחת.
        }

        String email = emailEditText.getText().toString().trim();
        // לוקח את האימייל שהמשתמש כתב.

        String password = passwordEditText.getText().toString().trim();
        // לוקח את הסיסמה שהמשתמש כתב.

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please enter email");
            return;
            // אם האימייל ריק עוצרים.
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter password");
            return;
            // אם הסיסמה ריקה עוצרים.
        }

        loginInProgress = true;
        // מסמן שהתחילה התחברות.

        loginButton.setEnabled(false);
        // מבטל את הכפתור עד שההתחברות תסתיים.

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        loginInProgress = false;
                        // מסמן שההתחברות הסתיימה.

                        loginButton.setEnabled(true);
                        // מחזיר את הכפתור לפעילות.

                        Toast.makeText(
                                Login.this,
                                "Login failed",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    String uid = mAuth.getCurrentUser() != null
                            ? mAuth.getCurrentUser().getUid()
                            : "";
                    // לוקח את מזהה המשתמש שהתחבר.

                    loadUserData(uid);
                    // מביא את פרטי המשתמש מהמסד.
                });
    }

    private void loadUserData(String uid) {
        // הפונקציה מביאה את פרטי המשתמש מה-Firebase Database.

        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {

            @Override
            public void onCompleted(User user) {

                loginInProgress = false;
                // מסמן שסיימנו להתחבר.

                loginButton.setEnabled(true);
                // מחזיר את הכפתור לפעילות.

                if (user == null) {

                    Toast.makeText(
                            Login.this,
                            "User data not found",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                SessionManager.saveUser(Login.this, user);
                // שומר את פרטי המשתמש ב-SharedPreferences.

                Toast.makeText(
                        Login.this,
                        "Login successful",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent;
                // ישמור את המסך שאליו נעבור.

                if (user.isAdmin()) {

                    intent = new Intent(
                            Login.this,
                            AdminPage.class
                    );
                    // אם המשתמש מנהל מעבירים לדף מנהל.

                } else {

                    intent = new Intent(
                            Login.this,
                            UserPage.class
                    );
                    // אם המשתמש רגיל מעבירים לדף משתמש.
                }

                startActivity(intent);
                // פותח את המסך המתאים.

                finish();
                // סוגר את מסך ההתחברות.
            }

            @Override
            public void onFailed(Exception e) {

                loginInProgress = false;
                // מסמן שסיימנו את הניסיון.

                loginButton.setEnabled(true);
                // מחזיר את הכפתור לפעילות.

                Toast.makeText(
                        Login.this,
                        "Failed to load user data",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}