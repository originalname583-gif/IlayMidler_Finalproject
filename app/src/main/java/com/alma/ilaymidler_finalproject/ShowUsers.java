package com.alma.ilaymidler_finalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.adapters.UserAdapter;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ShowUsers extends BaseMenuActivity {

    private UserAdapter userAdapter;
    // Adapter שמציג את המשתמשים ברשימה.

    private TextView tvUserCount, tvEmpty;
    // טקסטים שמציגים את מספר המשתמשים או הודעה אם אין משתמשים.

    private RecyclerView rvUsers;
    // הרשימה שבה מוצגים כל המשתמשים.

    private DatabaseService databaseService;
    // השירות שאחראי על פעולות מול Firebase.

    private boolean allowedToLoad = false;
    // שומר האם המשתמש הנוכחי הוא מנהל ומותר לו לראות משתמשים.

    private boolean operationInProgress = false;
    // מונע ביצוע כמה פעולות מנהל במקביל.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מפעיל את onCreate של המחלקה האב.

        setContentView(R.layout.activity_show_users);
        // טוען את העיצוב של מסך הצגת משתמשים.

        databaseService = DatabaseService.getInstance();
        // מקבל את DatabaseService כדי לעבוד מול Firebase.

        setupToolbar(R.id.topToolbar, "Users");
        // מגדיר Toolbar עם הכותרת Users.

        rvUsers = findViewById(R.id.rv_users);
        // מחבר את RecyclerView מה-XML לקוד.

        tvUserCount = findViewById(R.id.tv_user_count);
        // מחבר את טקסט מספר המשתמשים מה-XML לקוד.

        tvEmpty = findViewById(R.id.tvEmpty);
        // מחבר את טקסט ההודעה הריקה מה-XML לקוד.

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        // מגדיר שהמשתמשים יוצגו כרשימה אנכית.

        userAdapter = new UserAdapter(new UserAdapter.OnUserClickListener() {
            // יוצר Adapter עם פעולות לחיצה על משתמש.

            @Override
            public void onUserClick(User user) {
                if (user == null) return;
                // אם המשתמש לא תקין, לא עושים כלום.

                Toast.makeText(
                        ShowUsers.this,
                        user.isAdmin() ? "Admin user" : "Regular user",
                        Toast.LENGTH_SHORT
                ).show();
                // מציג האם המשתמש הוא מנהל או משתמש רגיל.
            }

            @Override
            public void onLongUserClick(User user) {
                if (!allowedToLoad || operationInProgress || user == null) return;
                // אם אין הרשאה, יש פעולה פעילה, או המשתמש לא תקין — לא ממשיכים.

                showAdminDialog(user);
                // פותח דיאלוג לשינוי הרשאת מנהל.
            }
        });

        rvUsers.setAdapter(userAdapter);
        // מחבר את ה-Adapter לרשימה.

        checkAdminAccess();
        // בודק האם המשתמש שמחובר הוא מנהל.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // מופעל בכל פעם שחוזרים למסך.

        if (allowedToLoad) {
            loadUsers();
            // אם כבר ידוע שהמשתמש מנהל, טוענים את רשימת המשתמשים.
        }
    }

    private void checkAdminAccess() {
        // הפונקציה בודקת אם המשתמש המחובר הוא מנהל.

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // מקבל את המשתמש שמחובר כרגע.

        if (firebaseUser == null) {
            startActivity(new Intent(ShowUsers.this, Login.class));
            // אם אין משתמש מחובר, מעבירים למסך התחברות.

            finish();
            // סוגרים את המסך הזה.

            return;
            // עוצרים את הפונקציה.
        }

        databaseService.getUser(firebaseUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
            // מביא את פרטי המשתמש מה-Database לפי ה-uid שלו.

            @Override
            public void onCompleted(User user) {
                if (user == null || !user.isAdmin()) {
                    allowedToLoad = false;
                    // אם המשתמש לא מנהל, אין הרשאה.

                    Toast.makeText(ShowUsers.this, "אין לך הרשאה להיכנס לעמוד מנהל", Toast.LENGTH_SHORT).show();
                    // מציג הודעה שאין הרשאה.

                    startActivity(new Intent(ShowUsers.this, UserPage.class));
                    // מעביר למסך משתמש רגיל.

                    finish();
                    // סוגר את המסך.

                    return;
                    // עוצר את הפונקציה.
                }

                allowedToLoad = true;
                // אם המשתמש מנהל, מותר לטעון משתמשים.

                loadUsers();
                // טוען את רשימת המשתמשים.
            }

            @Override
            public void onFailed(Exception e) {
                allowedToLoad = false;
                // במקרה של שגיאה לא נותנים הרשאת מנהל.

                Toast.makeText(ShowUsers.this, "שגיאה בבדיקת הרשאות", Toast.LENGTH_SHORT).show();
                // מציג הודעת שגיאה.

                startActivity(new Intent(ShowUsers.this, UserPage.class));
                // מחזיר למסך משתמש רגיל.

                finish();
                // סוגר את המסך.
            }
        });
    }

    private void loadUsers() {
        // הפונקציה טוענת את כל המשתמשים מ-Firebase.

        if (!allowedToLoad) return;
        // אם אין הרשאה, לא טוענים משתמשים.

        databaseService.getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            // מבקש את כל המשתמשים מהמסד.

            @Override
            public void onCompleted(List<User> users) {

                if (users == null || users.isEmpty()) {
                    tvUserCount.setText("Total users: 0");
                    // מציג שיש 0 משתמשים.

                    rvUsers.setVisibility(View.GONE);
                    // מסתיר את הרשימה.

                    tvEmpty.setVisibility(View.VISIBLE);
                    // מציג הודעה שאין משתמשים.

                    tvEmpty.setText("No users were found.");
                    // מגדיר את טקסט ההודעה.

                    userAdapter.setUserList(null);
                    // מנקה את הרשימה במסך.

                    return;
                    // עוצר את הפונקציה.
                }

                userAdapter.setUserList(users);
                // מעדכן את ה-Adapter עם רשימת המשתמשים.

                tvUserCount.setText("Total users: " + users.size());
                // מציג את מספר המשתמשים.

                rvUsers.setVisibility(View.VISIBLE);
                // מציג את רשימת המשתמשים.

                tvEmpty.setVisibility(View.GONE);
                // מסתיר את הודעת הריק.
            }

            @Override
            public void onFailed(Exception e) {
                rvUsers.setVisibility(View.GONE);
                // מסתיר את הרשימה.

                tvEmpty.setVisibility(View.VISIBLE);
                // מציג הודעת שגיאה.

                tvEmpty.setText("Something went wrong while loading users.");
                // מגדיר טקסט שגיאה.
            }
        });
    }

    private void showAdminDialog(User user) {
        // הפונקציה מציגה חלון שינוי הרשאת מנהל למשתמש.

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // מקבל את המשתמש שמחובר כרגע.

        if (currentFirebaseUser != null && currentFirebaseUser.getUid().equals(user.getId()) && user.isAdmin()) {
            Toast.makeText(this, "You can't remove admin access from yourself", Toast.LENGTH_SHORT).show();
            // מונע ממנהל להסיר לעצמו הרשאה בטעות.

            return;
            // עוצר את הפונקציה.
        }

        String action = user.isAdmin() ? "Remove admin access" : "Make admin";
        // קובע האם הפעולה היא להפוך למנהל או להסיר מנהל.

        String fullName = (user.getFirstName() + " " + user.getLastName()).trim();
        // בונה שם מלא לתצוגה.

        if (fullName.isEmpty()) {
            fullName = "User";
            // אם אין שם, מציג User.
        }

        new AlertDialog.Builder(this)
                .setTitle(fullName)
                // כותרת החלון היא שם המשתמש.

                .setMessage(action + "?")
                // שואל האם לבצע את הפעולה.

                .setPositiveButton("Yes", (dialog, which) -> {
                    if (operationInProgress) return;
                    // אם פעולה כבר מתבצעת, לא מתחילים עוד אחת.

                    operationInProgress = true;
                    // מסמן שהתחילה פעולה.

                    databaseService.setUserAdmin(user.getId(), !user.isAdmin(),
                            new DatabaseService.DatabaseCallback<Void>() {
                                // מעדכן את השדה isAdmin ב-Firebase.

                                @Override
                                public void onCompleted(Void object) {
                                    operationInProgress = false;
                                    // מסמן שהפעולה הסתיימה.

                                    Toast.makeText(ShowUsers.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                    // מציג הודעת הצלחה.

                                    loadUsers();
                                    // טוען מחדש את רשימת המשתמשים.
                                }

                                @Override
                                public void onFailed(Exception e) {
                                    operationInProgress = false;
                                    // מסמן שהפעולה הסתיימה גם אם נכשלה.

                                    Toast.makeText(ShowUsers.this, "Failed to update admin", Toast.LENGTH_SHORT).show();
                                    // מציג הודעת שגיאה.
                                }
                            });
                })

                .setNegativeButton("No", null)
                // כפתור ביטול שסוגר את החלון.

                .show();
        // מציג את חלון האישור.
    }
}