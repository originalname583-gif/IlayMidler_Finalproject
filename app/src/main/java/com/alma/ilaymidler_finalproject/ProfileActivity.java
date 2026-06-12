package com.alma.ilaymidler_finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.alma.ilaymidler_finalproject.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends BaseMenuActivity {

    private TextView tvFullName;
    // מציג את השם המלא.

    private TextView tvEmail;
    // מציג את האימייל.

    private TextView tvPhone;
    // מציג את הטלפון.

    private TextView tvRole;
    // מציג אם המשתמש Admin או User.

    private TextView tvEmpty;
    // מציג הודעות שגיאה.

    private ProgressBar progressBar;
    // גלגל טעינה בזמן שליפת נתונים.

    private DatabaseService databaseService;
    // גישה למסד הנתונים.

    private boolean loadingInProgress = false;
    // מונע טעינות כפולות.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // הפונקציה הראשונה שרצה כשהמסך נפתח.

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        // מציג את מסך הפרופיל.

        setupToolbar(R.id.topToolbar, "My Profile");
        // מפעיל את התפריט העליון.

        tvFullName = findViewById(R.id.tvFullName);
        // מחבר את תצוגת השם.

        tvEmail = findViewById(R.id.tvEmail);
        // מחבר את תצוגת האימייל.

        tvPhone = findViewById(R.id.tvPhone);
        // מחבר את תצוגת הטלפון.

        tvRole = findViewById(R.id.tvRole);
        // מחבר את תצוגת התפקיד.

        tvEmpty = findViewById(R.id.tvEmpty);
        // מחבר את תצוגת השגיאות.

        progressBar = findViewById(R.id.progressBar);
        // מחבר את גלגל הטעינה.

        databaseService = DatabaseService.getInstance();
        // מקבל גישה למסד הנתונים.

        showSavedProfile();
        // מציג קודם את הנתונים שנשמרו ב-SharedPreferences.
    }

    @Override
    protected void onResume() {
        // רץ בכל פעם שחוזרים למסך.

        super.onResume();

        loadProfile();
        // טוען את הנתונים העדכניים מ-Firebase.
    }

    private void showSavedProfile() {
        // מציג את הנתונים ששמורים בטלפון.

        String fullName =
                (SessionManager.getFirstName(this)
                        + " "
                        + SessionManager.getLastName(this))
                        .trim();
        // מחבר שם פרטי ושם משפחה.

        tvFullName.setText(
                fullName.isEmpty()
                        ? "No name"
                        : fullName
        );
        // מציג שם מלא.

        tvEmail.setText(
                SessionManager.getEmail(this).isEmpty()
                        ? "No email"
                        : SessionManager.getEmail(this)
        );
        // מציג אימייל.

        tvPhone.setText(
                SessionManager.getPhone(this).isEmpty()
                        ? "No phone"
                        : SessionManager.getPhone(this)
        );
        // מציג טלפון.

        tvRole.setText(
                SessionManager.isAdmin(this)
                        ? "Admin"
                        : "User"
        );
        // מציג סוג משתמש.
    }

    private void loadProfile() {
        // טוען את הפרופיל מ-Firebase.

        if (loadingInProgress) {
            return;
            // אם כבר יש טעינה פעילה עוצרים.
        }

        FirebaseUser firebaseUser =
                FirebaseAuth.getInstance().getCurrentUser();
        // לוקח את המשתמש המחובר.

        if (firebaseUser == null) {

            Toast.makeText(
                    this,
                    "You must log in first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            // סוגר את המסך אם אין משתמש מחובר.

            return;
        }

        loadingInProgress = true;
        // מסמן שהתחילה טעינה.

        progressBar.setVisibility(View.VISIBLE);
        // מציג גלגל טעינה.

        tvEmpty.setVisibility(View.GONE);
        // מסתיר הודעות שגיאה.

        databaseService.getUser(
                firebaseUser.getUid(),
                new DatabaseService.DatabaseCallback<User>() {

                    @Override
                    public void onCompleted(User user) {

                        loadingInProgress = false;
                        // מסמן שהטעינה הסתיימה.

                        progressBar.setVisibility(View.GONE);
                        // מסתיר את גלגל הטעינה.

                        if (user == null) {

                            tvEmpty.setVisibility(View.VISIBLE);

                            tvEmpty.setText(
                                    "Something went wrong while loading your profile."
                            );

                            return;
                        }

                        SessionManager.saveUser(
                                ProfileActivity.this,
                                user
                        );
                        // מעדכן את SharedPreferences לפי הנתונים ב-Firebase.

                        showSavedProfile();
                        // מציג את הנתונים המעודכנים.
                    }

                    @Override
                    public void onFailed(Exception e) {

                        loadingInProgress = false;
                        // מסמן שהטעינה הסתיימה.

                        progressBar.setVisibility(View.GONE);
                        // מסתיר את גלגל הטעינה.

                        tvEmpty.setVisibility(View.VISIBLE);

                        tvEmpty.setText(
                                "Something went wrong while loading your profile."
                        );
                        // מציג הודעת שגיאה.

                        Toast.makeText(
                                ProfileActivity.this,
                                "Failed to load profile",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}