package com.alma.ilaymidler_finalproject;

import android.content.Intent;
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
import com.alma.ilaymidler_finalproject.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends BaseMenuActivity implements View.OnClickListener {

    private static final String TAG = "Register";
    // משמש להודעות Log לצורך בדיקות.

    private EditText etFname;
    // שדה שם פרטי.

    private EditText etLname;
    // שדה שם משפחה.

    private EditText etMail;
    // שדה אימייל.

    private EditText etPhone;
    // שדה טלפון.

    private EditText etPassword;
    // שדה סיסמה.

    private Button btnSubmit;
    // כפתור הרשמה.

    private DatabaseService databaseService;
    // גישה למסד הנתונים.

    private FirebaseAuth mAuth;
    // גישה למערכת ההתחברות של Firebase.

    private boolean registerInProgress = false;
    // מונע לחיצות כפולות על כפתור הרשמה.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // הפונקציה הראשונה שרצה כשהמסך נפתח.

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        // מאפשר שימוש מלא בשטח המסך.

        setContentView(R.layout.activity_register);
        // מציג את מסך ההרשמה.

        setupToolbar(R.id.topToolbar, "Register");
        // מציג את התפריט העליון.

        View mainView = findViewById(R.id.main);

        if (mainView != null) {

            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {

                Insets systemBars =
                        insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // לוקח את הגודל של שורת המצב והניווט.

                v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                );
                // מוסיף רווחים כדי שלא יהיו חפיפות.

                return insets;
            });
        }

        databaseService = DatabaseService.getInstance();
        // מקבל גישה למסד הנתונים.

        mAuth = FirebaseAuth.getInstance();
        // מקבל גישה ל-Firebase Authentication.

        etFname = findViewById(R.id.etFirstName);
        // מחבר את שדה השם הפרטי.

        etLname = findViewById(R.id.etLastName);
        // מחבר את שדה שם המשפחה.

        etMail = findViewById(R.id.etEmail);
        // מחבר את שדה האימייל.

        etPhone = findViewById(R.id.etPhone);
        // מחבר את שדה הטלפון.

        etPassword = findViewById(R.id.etPassword);
        // מחבר את שדה הסיסמה.

        btnSubmit = findViewById(R.id.btnRegister);
        // מחבר את כפתור ההרשמה.

        btnSubmit.setOnClickListener(this);
        // כאשר לוחצים על הכפתור מפעילים את onClick().
    }

    @Override
    public void onClick(View v) {
        // מופעל כאשר לוחצים על כפתור ההרשמה.

        if (registerInProgress) {
            return;
            // אם כבר מתבצעת הרשמה לא מתחילים עוד אחת.
        }

        String fName = etFname.getText().toString().trim();
        // לוקח את השם הפרטי.

        String lName = etLname.getText().toString().trim();
        // לוקח את שם המשפחה.

        String email = etMail.getText().toString().trim();
        // לוקח את האימייל.

        String phone = etPhone.getText().toString().trim();
        // לוקח את הטלפון.

        String password = etPassword.getText().toString().trim();
        // לוקח את הסיסמה.

        if (fName.isEmpty()) {
            etFname.setError("Enter first name");
            return;
        }

        if (lName.isEmpty()) {
            etLname.setError("Enter last name");
            return;
        }

        if (email.isEmpty()) {
            etMail.setError("Enter email");
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Enter phone");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter password");
            return;
        }

        registerUser(fName, lName, phone, email, password);
        // מתחיל את תהליך ההרשמה.
    }

    private void registerUser(String fname,
                              String lname,
                              String phone,
                              String email,
                              String password) {

        // הפונקציה יוצרת משתמש חדש ב-Firebase Authentication.

        registerInProgress = true;
        // מסמן שההרשמה התחילה.

        btnSubmit.setEnabled(false);
        // מבטל את הכפתור זמנית.

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {

                    if (!authTask.isSuccessful()
                            || mAuth.getCurrentUser() == null) {

                        registerInProgress = false;
                        // מסמן שההרשמה הסתיימה.

                        btnSubmit.setEnabled(true);
                        // מחזיר את הכפתור לפעילות.

                        Toast.makeText(
                                Register.this,
                                "Email already exists or invalid",
                                Toast.LENGTH_SHORT
                        ).show();

                        Log.e(TAG,
                                "FirebaseAuth Error",
                                authTask.getException());

                        return;
                    }

                    String uid =
                            mAuth.getCurrentUser().getUid();
                    // לוקח את ה-uid של המשתמש החדש.

                    User user =
                            new User(
                                    uid,
                                    fname,
                                    lname,
                                    email,
                                    phone,
                                    password,
                                    false
                            );
                    // יוצר אובייקט משתמש.

                    createUserInDatabase(user);
                    // שומר את המשתמש במסד הנתונים.
                });
    }

    private void createUserInDatabase(User user) {
        // שומר את המשתמש ב-Realtime Database.

        databaseService.createNewUser(
                user,
                new DatabaseService.DatabaseCallback<Void>() {

                    @Override
                    public void onCompleted(Void object) {

                        registerInProgress = false;
                        // מסמן שסיימנו הרשמה.

                        btnSubmit.setEnabled(true);
                        // מחזיר את הכפתור לפעילות.

                        SessionManager.saveUser(
                                Register.this,
                                user
                        );
                        // שומר את כל פרטי המשתמש ב-SharedPreferences.
                        // זה התיקון החשוב שחסר אצלך.

                        Toast.makeText(
                                Register.this,
                                "Registration successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        startActivity(
                                new Intent(
                                        Register.this,
                                        UserPage.class
                                )
                        );
                        // מעביר לדף המשתמש.

                        finish();
                        // סוגר את מסך ההרשמה.
                    }

                    @Override
                    public void onFailed(Exception e) {

                        registerInProgress = false;
                        // מסמן שהפעולה הסתיימה.

                        btnSubmit.setEnabled(true);
                        // מחזיר את הכפתור לפעילות.

                        Toast.makeText(
                                Register.this,
                                "Failed to register user",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}