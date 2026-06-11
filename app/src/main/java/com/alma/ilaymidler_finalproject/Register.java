package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
    // שם קבוע שמשמש לזיהוי הודעות Log של המסך הזה.

    private EditText etFname, etLname, etMail, etPhone, etPassword;
    // שדות טקסט שהמשתמש ממלא בהרשמה.

    private Button btnSubmit;
    // כפתור ההרשמה.

    private DatabaseService databaseService;
    // השירות שאחראי על שמירה וקריאה מ-Firebase Database.

    private FirebaseAuth mAuth;
    // השירות שאחראי על הרשמה והתחברות דרך Firebase Authentication.

    private boolean registerInProgress = false;
    // מונע מהמשתמש ללחוץ כמה פעמים על כפתור ההרשמה.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מפעיל את onCreate של המחלקה האב.

        EdgeToEdge.enable(this);
        // מאפשר לעיצוב להיכנס עד קצוות המסך.

        setContentView(R.layout.activity_register);
        // טוען את קובץ העיצוב של מסך ההרשמה.

        setupToolbar(R.id.topToolbar, "Register");
        // מגדיר Toolbar עם הכותרת Register.

        View mainView = findViewById(R.id.main);
        // מחפש את ה-Layout הראשי במסך.

        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                // מתאים את הריווח של המסך לפי הפס העליון והתחתון של המכשיר.

                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // מקבל את הגודל של אזורי המערכת.

                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                // מוסיף ריווח כדי שהתוכן לא ייכנס מתחת לסטטוס בר או ניווט.

                return insets;
                // מחזיר את ה-insets לאחר הטיפול.
            });
        }

        databaseService = DatabaseService.getInstance();
        // מקבל את DatabaseService כדי לשמור משתמש חדש במסד.

        mAuth = FirebaseAuth.getInstance();
        // מקבל את FirebaseAuth כדי ליצור משתמש חדש.

        etFname = findViewById(R.id.etFirstName);
        // מחבר את שדה השם הפרטי מה-XML לקוד.

        etLname = findViewById(R.id.etLastName);
        // מחבר את שדה שם המשפחה מה-XML לקוד.

        etMail = findViewById(R.id.etEmail);
        // מחבר את שדה האימייל מה-XML לקוד.

        etPhone = findViewById(R.id.etPhone);
        // מחבר את שדה הטלפון מה-XML לקוד.

        etPassword = findViewById(R.id.etPassword);
        // מחבר את שדה הסיסמה מה-XML לקוד.

        btnSubmit = findViewById(R.id.btnRegister);
        // מחבר את כפתור ההרשמה מה-XML לקוד.

        btnSubmit.setOnClickListener(this);
        // מגדיר שהמסך הזה יטפל בלחיצה על כפתור הרשמה.
    }

    @Override
    public void onClick(View v) {
        // הפונקציה מופעלת כאשר המשתמש לוחץ על כפתור ההרשמה.

        if (v.getId() == R.id.btnRegister) {
            validateAndRegister();
            // אם נלחץ כפתור ההרשמה, בודקים את הנתונים ומרשמים.
        }
    }

    private void validateAndRegister() {
        // הפונקציה בודקת שהנתונים תקינים לפני הרשמה.

        if (registerInProgress) {
            return;
            // אם כבר מתבצעת הרשמה, לא מאפשרים לחיצה נוספת.
        }

        String fName = etFname.getText().toString().trim();
        // לוקח את השם הפרטי מהשדה.

        String lName = etLname.getText().toString().trim();
        // לוקח את שם המשפחה מהשדה.

        String email = etMail.getText().toString().trim();
        // לוקח את האימייל מהשדה.

        String phone = etPhone.getText().toString().trim();
        // לוקח את מספר הטלפון מהשדה.

        String password = etPassword.getText().toString().trim();
        // לוקח את הסיסמה מהשדה.

        if (TextUtils.isEmpty(fName)) {
            etFname.setError("Enter first name");
            return;
            // אם השם הפרטי ריק, מציג שגיאה.
        }

        if (TextUtils.isEmpty(lName)) {
            etLname.setError("Enter last name");
            return;
            // אם שם המשפחה ריק, מציג שגיאה.
        }

        if (TextUtils.isEmpty(email)) {
            etMail.setError("Enter email");
            return;
            // אם האימייל ריק, מציג שגיאה.
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etMail.setError("Invalid email");
            return;
            // אם האימייל לא בפורמט תקין, מציג שגיאה.
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Enter phone");
            return;
            // אם הטלפון ריק, מציג שגיאה.
        }

        if (phone.length() < 9) {
            etPhone.setError("Invalid phone");
            return;
            // אם הטלפון קצר מדי, מציג שגיאה.
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password");
            return;
            // אם הסיסמה ריקה, מציג שגיאה.
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
            // Firebase דורש סיסמה באורך מינימלי של 6 תווים.
        }

        registerUser(fName, lName, phone, email, password);
        // אם הכל תקין, מתחילים הרשמה.
    }

    private void registerUser(String fname, String lname, String phone, String email, String password) {
        // הפונקציה יוצרת משתמש חדש ב-Firebase Authentication.

        registerInProgress = true;
        // מסמן שהתחילה הרשמה.

        btnSubmit.setEnabled(false);
        // מבטל זמנית את הכפתור כדי למנוע לחיצות כפולות.

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    // שולח בקשה ל-Firebase ליצור משתמש חדש.

                    if (!authTask.isSuccessful() || mAuth.getCurrentUser() == null) {
                        registerInProgress = false;
                        // מסמן שההרשמה הסתיימה.

                        btnSubmit.setEnabled(true);
                        // מחזיר את הכפתור לפעולה.

                        Toast.makeText(Register.this, "Email already exists or invalid!", Toast.LENGTH_SHORT).show();
                        // מציג הודעה אם האימייל כבר קיים או לא תקין.

                        Log.e(TAG, "FirebaseAuth Error: ", authTask.getException());
                        // מדפיס את השגיאה ל-Logcat.

                        return;
                        // עוצר את הפונקציה.
                    }

                    String uid = mAuth.getCurrentUser().getUid();
                    // מקבל את מזהה המשתמש החדש מ-FirebaseAuth.

                    User user = new User(uid, fname, lname, email, phone, password, false);
                    // יוצר אובייקט משתמש חדש.
                    // false אומר שהמשתמש הוא לא מנהל.

                    createUserInDatabase(user);
                    // שומר את פרטי המשתמש ב-Firebase Database.
                });
    }

    private void createUserInDatabase(User user) {
        // הפונקציה שומרת את המשתמש החדש ב-Firebase Realtime Database.

        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            // שולח את המשתמש לשמירה במסד.

            @Override
            public void onCompleted(Void object) {
                registerInProgress = false;
                // מסמן שההרשמה הסתיימה.

                btnSubmit.setEnabled(true);
                // מחזיר את כפתור ההרשמה לפעולה.

                Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                // מציג הודעת הצלחה.

                startActivity(new Intent(Register.this, UserPage.class));
                // מעביר את המשתמש לעמוד המשתמש.

                finish();
                // סוגר את מסך ההרשמה.
            }

            @Override
            public void onFailed(Exception e) {
                registerInProgress = false;
                // מסמן שההרשמה הסתיימה גם אם נכשלה.

                btnSubmit.setEnabled(true);
                // מחזיר את הכפתור לפעולה.

                Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                // מציג הודעת שגיאה.
            }
        });
    }
}