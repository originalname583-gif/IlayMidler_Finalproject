package com.alma.ilaymidler_finalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.adapters.ManageCourtsAdapter;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ManageCourtsActivity extends BaseMenuActivity {

    private RecyclerView rvManageCourts;
    // הרשימה שמציגה את כל המגרשים לניהול.

    private TextView tvEmpty;
    // טקסט שמוצג אם אין מגרשים או אם יש שגיאה.

    private ProgressBar progressBar;
    // סימן טעינה בזמן שמביאים נתונים מ-Firebase.

    private DatabaseService databaseService;
    // השירות שאחראי על פעולות מול Firebase.

    private ManageCourtsAdapter adapter;
    // Adapter שמציג את המגרשים ואת כפתורי העריכה והמחיקה.

    private boolean allowedToLoad = false;
    // קובע האם המשתמש הוא מנהל ומותר לו לטעון את המגרשים.

    private boolean operationInProgress = false;
    // מונע לחיצות כפולות בזמן עריכה או מחיקה.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מפעיל את onCreate של המחלקה האב.

        setContentView(R.layout.activity_manage_courts);
        // טוען את העיצוב של מסך ניהול המגרשים.

        databaseService = DatabaseService.getInstance();
        // מקבל את DatabaseService כדי לעבוד מול Firebase.

        setupToolbar(R.id.topToolbar, "Manage Courts");
        // מגדיר Toolbar עם הכותרת Manage Courts.

        rvManageCourts = findViewById(R.id.rvManageCourts);
        // מחבר את RecyclerView מה-XML לקוד.

        tvEmpty = findViewById(R.id.tvEmpty);
        // מחבר את טקסט הריק מה-XML לקוד.

        progressBar = findViewById(R.id.progressBar);
        // מחבר את סימן הטעינה מה-XML לקוד.

        rvManageCourts.setLayoutManager(new LinearLayoutManager(this));
        // מגדיר שהמגרשים יוצגו כרשימה אנכית.

        adapter = new ManageCourtsAdapter(new ManageCourtsAdapter.OnCourtActionListener() {
            // יוצר Adapter עם פעולות עריכה ומחיקה.

            @Override
            public void onEdit(Court court) {
                if (!allowedToLoad || operationInProgress || court == null) return;
                // אם אין הרשאה, פעולה כבר רצה, או שהמגרש לא תקין — לא עושים כלום.

                showEditDialog(court);
                // פותח חלון עריכת מגרש.
            }

            @Override
            public void onDelete(Court court) {
                if (!allowedToLoad || operationInProgress || court == null) return;
                // אם אין הרשאה, פעולה כבר רצה, או שהמגרש לא תקין — לא עושים כלום.

                showDeleteDialog(court);
                // פותח חלון אישור מחיקה.
            }
        });

        rvManageCourts.setAdapter(adapter);
        // מחבר את ה-Adapter לרשימה.

        checkAdminAccess();
        // בודק האם המשתמש שמחובר הוא מנהל.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // מופעל בכל פעם שחוזרים למסך.

        if (allowedToLoad) {
            loadCourts();
            // אם המשתמש מנהל, טוענים מחדש את המגרשים.
        }
    }

    private void checkAdminAccess() {
        // הפונקציה בודקת האם המשתמש המחובר הוא מנהל.

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // מקבל את המשתמש שמחובר כרגע.

        if (firebaseUser == null) {
            startActivity(new Intent(ManageCourtsActivity.this, Login.class));
            // אם אין משתמש מחובר, מעבירים למסך התחברות.

            finish();
            // סוגרים את המסך הנוכחי.

            return;
            // עוצרים את הפונקציה.
        }

        databaseService.getUser(firebaseUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
            // מביא את פרטי המשתמש מה-Database לפי ה-uid.

            @Override
            public void onCompleted(User user) {
                if (user == null || !user.isAdmin()) {
                    allowedToLoad = false;
                    // אם המשתמש לא מנהל, אין הרשאה.

                    Toast.makeText(ManageCourtsActivity.this, "אין לך הרשאה להיכנס לעמוד מנהל", Toast.LENGTH_SHORT).show();
                    // מציג הודעה שאין הרשאה.

                    startActivity(new Intent(ManageCourtsActivity.this, UserPage.class));
                    // מעביר לעמוד משתמש רגיל.

                    finish();
                    // סוגר את מסך הניהול.

                    return;
                    // עוצר את המשך הפעולה.
                }

                allowedToLoad = true;
                // אם המשתמש מנהל, מאפשרים טעינת מגרשים.

                loadCourts();
                // טוענים את המגרשים.
            }

            @Override
            public void onFailed(Exception e) {
                allowedToLoad = false;
                // במקרה של שגיאה לא נותנים הרשאת מנהל.

                Toast.makeText(ManageCourtsActivity.this, "שגיאה בבדיקת הרשאות", Toast.LENGTH_SHORT).show();
                // מציג הודעת שגיאה.

                startActivity(new Intent(ManageCourtsActivity.this, UserPage.class));
                // מחזיר לעמוד משתמש רגיל.

                finish();
                // סוגר את המסך.
            }
        });
    }

    private void loadCourts() {
        // הפונקציה טוענת את כל המגרשים מ-Firebase.

        if (!allowedToLoad) return;
        // אם אין הרשאה, לא טוענים מגרשים.

        progressBar.setVisibility(View.VISIBLE);
        // מציג טעינה.

        tvEmpty.setVisibility(View.GONE);
        // מסתיר הודעת ריק/שגיאה.

        rvManageCourts.setVisibility(View.GONE);
        // מסתיר זמנית את הרשימה בזמן טעינה.

        databaseService.getCourtsList(new DatabaseService.DatabaseCallback<List<Court>>() {
            // מבקש את כל המגרשים מ-Firebase.

            @Override
            public void onCompleted(List<Court> courts) {
                progressBar.setVisibility(View.GONE);
                // מסתיר טעינה.

                if (courts == null || courts.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    // מציג טקסט אם אין מגרשים.

                    tvEmpty.setText("No courts found");
                    // מציג הודעה שאין מגרשים.

                    rvManageCourts.setVisibility(View.GONE);
                    // מסתיר את הרשימה.

                    return;
                    // עוצר את הפונקציה.
                }

                adapter.updateList(courts);
                // מעדכן את הרשימה במסך.

                tvEmpty.setVisibility(View.GONE);
                // מסתיר את הודעת הריק.

                rvManageCourts.setVisibility(View.VISIBLE);
                // מציג את הרשימה.
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                // מסתיר טעינה.

                rvManageCourts.setVisibility(View.GONE);
                // מסתיר את הרשימה.

                tvEmpty.setVisibility(View.VISIBLE);
                // מציג הודעת שגיאה.

                tvEmpty.setText("Failed to load courts");
                // מגדיר טקסט שגיאה.

                Toast.makeText(ManageCourtsActivity.this, "Failed to load courts", Toast.LENGTH_SHORT).show();
                // מציג הודעת Toast.
            }
        });
    }

    private void showEditDialog(Court court) {
        // הפונקציה פותחת חלון לעריכת פרטי מגרש.

        LinearLayout layout = new LinearLayout(this);
        // יוצר Layout שיכיל את שדות העריכה.

        layout.setOrientation(LinearLayout.VERTICAL);
        // מסדר את השדות אחד מתחת לשני.

        int pad = 40;
        // גודל ריווח פנימי לחלון.

        layout.setPadding(pad, pad, pad, pad);
        // מוסיף ריווח פנימי.

        EditText etName = new EditText(this);
        // שדה לעריכת שם המגרש.

        etName.setHint("Court name");
        // רמז לשדה שם.

        etName.setText(court.getName());
        // מציב את השם הנוכחי של המגרש.

        EditText etCity = new EditText(this);
        // שדה לעריכת העיר.

        etCity.setHint("City");
        // רמז לשדה עיר.

        etCity.setText(court.getCity());
        // מציב את העיר הנוכחית.

        EditText etAddress = new EditText(this);
        // שדה לעריכת הכתובת.

        etAddress.setHint("Address");
        // רמז לשדה כתובת.

        etAddress.setText(court.getAddress());
        // מציב את הכתובת הנוכחית.

        EditText etType = new EditText(this);
        // שדה לעריכת סוג המגרש.

        etType.setHint("Type");
        // רמז לשדה סוג.

        etType.setText(court.getType());
        // מציב את סוג המגרש הנוכחי.

        layout.addView(etName);
        // מוסיף את שדה השם לחלון.

        layout.addView(etCity);
        // מוסיף את שדה העיר לחלון.

        layout.addView(etAddress);
        // מוסיף את שדה הכתובת לחלון.

        layout.addView(etType);
        // מוסיף את שדה הסוג לחלון.

        new AlertDialog.Builder(this)
                .setTitle("Edit Court")
                // כותרת החלון.

                .setView(layout)
                // מכניס את השדות לחלון.

                .setPositiveButton("Save", (dialog, which) -> {
                    if (!allowedToLoad || operationInProgress) return;
                    // אם אין הרשאה או שכבר יש פעולה פעילה, עוצרים.

                    String newName = etName.getText().toString().trim();
                    // לוקח שם חדש.

                    String newCity = etCity.getText().toString().trim();
                    // לוקח עיר חדשה.

                    String newAddress = etAddress.getText().toString().trim();
                    // לוקח כתובת חדשה.

                    String newType = etType.getText().toString().trim();
                    // לוקח סוג חדש.

                    if (newName.isEmpty() || newCity.isEmpty() || newAddress.isEmpty() || newType.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        // אם אחד השדות ריק, מציג הודעה.

                        return;
                        // עוצר את השמירה.
                    }

                    operationInProgress = true;
                    // מסמן שהתחילה פעולת עדכון.

                    court.setName(newName);
                    // מעדכן שם מגרש באובייקט.

                    court.setCity(newCity);
                    // מעדכן עיר.

                    court.setAddress(newAddress);
                    // מעדכן כתובת.

                    court.setType(newType);
                    // מעדכן סוג.

                    databaseService.updateCourt(court, new DatabaseService.DatabaseCallback<Void>() {
                        // שולח את המגרש המעודכן ל-Firebase.

                        @Override
                        public void onCompleted(Void object) {
                            operationInProgress = false;
                            // מסמן שהפעולה הסתיימה.

                            Toast.makeText(ManageCourtsActivity.this, "Court updated", Toast.LENGTH_SHORT).show();
                            // מציג הודעת הצלחה.

                            loadCourts();
                            // מרענן את רשימת המגרשים.
                        }

                        @Override
                        public void onFailed(Exception e) {
                            operationInProgress = false;
                            // מסמן שהפעולה הסתיימה גם אם נכשלה.

                            Toast.makeText(ManageCourtsActivity.this, "Failed to update court", Toast.LENGTH_SHORT).show();
                            // מציג הודעת שגיאה.
                        }
                    });
                })

                .setNegativeButton("Cancel", null)
                // כפתור ביטול שסוגר את החלון.

                .show();
        // מציג את החלון.
    }

    private void showDeleteDialog(Court court) {
        // הפונקציה פותחת חלון אישור למחיקת מגרש.

        new AlertDialog.Builder(this)
                .setTitle("Delete Court")
                // כותרת החלון.

                .setMessage("Are you sure you want to delete " + court.getName() + "?")
                // הודעה שמוודאת שהמנהל באמת רוצה למחוק.

                .setPositiveButton("Delete", (dialog, which) -> {
                    if (!allowedToLoad || operationInProgress) return;
                    // אם אין הרשאה או פעולה כבר רצה, לא ממשיכים.

                    operationInProgress = true;
                    // מסמן שהתחילה פעולת מחיקה.

                    databaseService.deleteCourt(court.getId(), new DatabaseService.DatabaseCallback<Void>() {
                        // מוחק את המגרש מ-Firebase.

                        @Override
                        public void onCompleted(Void object) {
                            operationInProgress = false;
                            // מסמן שהמחיקה הסתיימה.

                            Toast.makeText(ManageCourtsActivity.this, "Court deleted", Toast.LENGTH_SHORT).show();
                            // מציג הודעת הצלחה.

                            loadCourts();
                            // מרענן את הרשימה.
                        }

                        @Override
                        public void onFailed(Exception e) {
                            operationInProgress = false;
                            // מסמן שהפעולה הסתיימה גם אם נכשלה.

                            Toast.makeText(ManageCourtsActivity.this, "Failed to delete court", Toast.LENGTH_SHORT).show();
                            // מציג הודעת שגיאה.
                        }
                    });
                })

                .setNegativeButton("Cancel", null)
                // כפתור ביטול.

                .show();
        // מציג את חלון האישור.
    }
}