package com.alma.ilaymidler_finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends BaseMenuActivity {

    private TextView tvFullName, tvEmail, tvPhone, tvRole, tvEmpty;
    // טקסטים שמציגים את פרטי המשתמש ואת הודעת השגיאה/ריק.

    private ProgressBar progressBar;
    // סימן טעינה בזמן שמביאים את הפרופיל מ-Firebase.

    private DatabaseService databaseService;
    // השירות שאחראי על פעולות מול Firebase.

    private boolean loadingInProgress = false;
    // מונע טעינה כפולה של הפרופיל באותו זמן.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מפעיל את onCreate של המחלקה האב.

        setContentView(R.layout.activity_profile);
        // טוען את קובץ העיצוב של מסך הפרופיל.

        setupToolbar(R.id.topToolbar, "My Profile");
        // מגדיר Toolbar עם הכותרת My Profile.

        tvFullName = findViewById(R.id.tvFullName);
        // מחבר את שדה השם המלא מה-XML לקוד.

        tvEmail = findViewById(R.id.tvEmail);
        // מחבר את שדה האימייל מה-XML לקוד.

        tvPhone = findViewById(R.id.tvPhone);
        // מחבר את שדה הטלפון מה-XML לקוד.

        tvRole = findViewById(R.id.tvRole);
        // מחבר את שדה התפקיד מה-XML לקוד.

        tvEmpty = findViewById(R.id.tvEmpty);
        // מחבר את הודעת השגיאה/ריק מה-XML לקוד.

        progressBar = findViewById(R.id.progressBar);
        // מחבר את סימן הטעינה מה-XML לקוד.

        databaseService = DatabaseService.getInstance();
        // מקבל את DatabaseService כדי לקרוא נתונים מ-Firebase.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // מופעל בכל פעם שחוזרים למסך הפרופיל.

        loadProfile();
        // טוען את פרטי המשתמש מחדש.
    }

    private void loadProfile() {
        // הפונקציה טוענת את פרטי הפרופיל של המשתמש המחובר.

        if (loadingInProgress) {
            return;
            // אם כבר יש טעינה פעילה, לא מתחילים אחת נוספת.
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // מקבל את המשתמש שמחובר כרגע.

        if (firebaseUser == null) {
            Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            // אם אין משתמש מחובר, מציג הודעה.

            finish();
            // סוגר את מסך הפרופיל.

            return;
            // עוצר את הפונקציה.
        }

        loadingInProgress = true;
        // מסמן שהתחילה טעינה.

        String userId = firebaseUser.getUid();
        // שומר את מזהה המשתמש המחובר.

        progressBar.setVisibility(View.VISIBLE);
        // מציג טעינה.

        tvEmpty.setVisibility(View.GONE);
        // מסתיר הודעת שגיאה/ריק בזמן טעינה.

        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            // מביא את המשתמש מה-Database לפי ה-id שלו.

            @Override
            public void onCompleted(User user) {
                loadingInProgress = false;
                // מסמן שהטעינה הסתיימה.

                progressBar.setVisibility(View.GONE);
                // מסתיר טעינה.

                if (user == null) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    // מציג הודעת שגיאה.

                    tvEmpty.setText("Something went wrong while loading your profile.");
                    // כותב הודעת שגיאה.

                    return;
                    // עוצר את הפונקציה.
                }

                String fullName = (user.getFirstName() + " " + user.getLastName()).trim();
                // בונה שם מלא מהשם הפרטי ושם המשפחה.

                tvFullName.setText(fullName.isEmpty() ? "No name" : fullName);
                // מציג שם מלא או No name אם אין שם.

                tvEmail.setText(user.getEmail().isEmpty() ? "No email" : user.getEmail());
                // מציג אימייל או No email אם אין.

                tvPhone.setText(user.getPhone().isEmpty() ? "No phone" : user.getPhone());
                // מציג טלפון או No phone אם אין.

                tvRole.setText(user.isAdmin() ? "Admin" : "User");
                // מציג האם המשתמש הוא מנהל או משתמש רגיל.
            }

            @Override
            public void onFailed(Exception e) {
                loadingInProgress = false;
                // מסמן שהטעינה הסתיימה גם אם נכשלה.

                progressBar.setVisibility(View.GONE);
                // מסתיר טעינה.

                tvEmpty.setVisibility(View.VISIBLE);
                // מציג הודעת שגיאה.

                tvEmpty.setText("Something went wrong while loading your profile.");
                // שם טקסט שגיאה.

                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                // מציג הודעת Toast.
            }
        });
    }
}