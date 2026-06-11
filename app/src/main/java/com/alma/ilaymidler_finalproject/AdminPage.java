package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminPage extends BaseMenuActivity {

    private Button btnAddCourt, btnShowUsers, btnManageCourts, btnLogout;
    // כפתורים של עמוד המנהל.

    private boolean isAdminAllowed = false;
    // שומר האם המשתמש באמת מנהל.
    // עד שהבדיקה מסתיימת, לא מאפשרים כניסה לעמודי ניהול.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מפעיל את onCreate של המחלקה האב.

        setContentView(R.layout.activity_admin_page);
        // טוען את העיצוב של עמוד המנהל.

        setupToolbar(R.id.topToolbar, "Admin Dashboard");
        // מגדיר את ה-Toolbar עם כותרת של עמוד מנהל.

        btnAddCourt = findViewById(R.id.btnAddCourt);
        // מחבר את כפתור הוספת המגרש מה-XML לקוד.

        btnShowUsers = findViewById(R.id.btnShowUsers);
        // מחבר את כפתור הצגת המשתמשים מה-XML לקוד.

        btnManageCourts = findViewById(R.id.btnManageCourts);
        // מחבר את כפתור ניהול המגרשים מה-XML לקוד.

        btnLogout = findViewById(R.id.btnLogout);
        // מחבר את כפתור ההתנתקות מה-XML לקוד.

        setAdminButtonsEnabled(false);
        // חוסם את כפתורי הניהול עד שמוודאים שהמשתמש מנהל.

        checkAdminAccess();
        // בודק האם המשתמש שמחובר הוא מנהל.

        btnAddCourt.setOnClickListener(v -> {
            if (!isAdminAllowed) return;
            // אם המשתמש לא מנהל, לא נותנים לו להיכנס.

            startActivity(new Intent(AdminPage.this, AddItem.class));
            // פותח את מסך הוספת מגרש.
        });

        btnShowUsers.setOnClickListener(v -> {
            if (!isAdminAllowed) return;
            // אם המשתמש לא מנהל, לא נותנים לו להיכנס.

            startActivity(new Intent(AdminPage.this, ShowUsers.class));
            // פותח את מסך רשימת המשתמשים.
        });

        btnManageCourts.setOnClickListener(v -> {
            if (!isAdminAllowed) return;
            // אם המשתמש לא מנהל, לא נותנים לו להיכנס.

            startActivity(new Intent(AdminPage.this, ManageCourtsActivity.class));
            // פותח את מסך ניהול המגרשים.
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            // מנתק את המשתמש מ-Firebase.

            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            // יוצר מעבר למסך הראשי.

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // מנקה מסכים קודמים כדי שלא יהיה אפשר לחזור אחורה לעמוד מנהל.

            startActivity(intent);
            // פותח את המסך הראשי.

            finish();
            // סוגר את עמוד המנהל.
        });
    }

    private void setAdminButtonsEnabled(boolean enabled) {
        // הפונקציה מפעילה או מכבה את כפתורי הניהול.

        btnAddCourt.setEnabled(enabled);
        // מפעיל או מכבה את כפתור הוספת מגרש.

        btnShowUsers.setEnabled(enabled);
        // מפעיל או מכבה את כפתור הצגת משתמשים.

        btnManageCourts.setEnabled(enabled);
        // מפעיל או מכבה את כפתור ניהול מגרשים.
    }

    private void checkAdminAccess() {
        // הפונקציה בודקת האם המשתמש המחובר הוא מנהל.

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // מקבל את המשתמש שמחובר כרגע דרך Firebase Authentication.

        if (firebaseUser == null) {
            startActivity(new Intent(AdminPage.this, Login.class));
            // אם אין משתמש מחובר, מעבירים למסך התחברות.

            finish();
            // סוגרים את עמוד המנהל.

            return;
            // עוצרים את המשך הפונקציה.
        }

        DatabaseService.getInstance().getUser(firebaseUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
            // מביא את פרטי המשתמש מה-Database לפי ה-uid שלו.

            @Override
            public void onCompleted(User user) {
                if (user == null || !user.isAdmin()) {
                    Toast.makeText(AdminPage.this, "אין לך הרשאה להיכנס לעמוד מנהל", Toast.LENGTH_SHORT).show();
                    // מציג הודעה שאין הרשאה.

                    startActivity(new Intent(AdminPage.this, UserPage.class));
                    // מעביר משתמש רגיל לעמוד המשתמש.

                    finish();
                    // סוגר את עמוד המנהל.

                    return;
                    // עוצר את המשך הפונקציה.
                }

                isAdminAllowed = true;
                // אם המשתמש מנהל, שומרים שיש לו הרשאה.

                setAdminButtonsEnabled(true);
                // מאפשרים את כפתורי הניהול.
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AdminPage.this, "שגיאה בבדיקת הרשאות", Toast.LENGTH_SHORT).show();
                // מציג הודעת שגיאה.

                startActivity(new Intent(AdminPage.this, UserPage.class));
                // במקרה של שגיאה, מחזירים לעמוד משתמש רגיל.

                finish();
                // סוגרים את עמוד המנהל.
            }
        });
    }
}