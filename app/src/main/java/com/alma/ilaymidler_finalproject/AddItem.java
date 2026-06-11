package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddItem extends BaseMenuActivity {

    private EditText etItemName, etItemInfo;
    // שדות טקסט שבהם המנהל כותב את שם המגרש והמידע/כתובת.

    private Spinner spLocation, spType;
    // רשימות בחירה לעיר ולסוג המגרש.

    private Button btnAddItem;
    // כפתור שמוסיף את המגרש.

    private boolean isAdminAllowed = false;
    // משתנה שבודק האם המשתמש באמת מנהל.
    // עד שלא מסיימים לבדוק הרשאה, לא נותנים להוסיף מגרש.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מפעיל את onCreate של המחלקה האב.

        setContentView(R.layout.activity_add_item);
        // טוען את קובץ העיצוב של המסך.

        setupToolbar(R.id.topToolbar, "Add Court");
        // מגדיר Toolbar עם הכותרת Add Court.

        etItemName = findViewById(R.id.etItemName);
        // מחבר את שדה שם המגרש מה-XML לקוד.

        etItemInfo = findViewById(R.id.etItemInfo);
        // מחבר את שדה המידע/כתובת מה-XML לקוד.

        spLocation = findViewById(R.id.spLocation);
        // מחבר את רשימת הערים מה-XML לקוד.

        spType = findViewById(R.id.spType);
        // מחבר את רשימת סוגי המגרשים מה-XML לקוד.

        btnAddItem = findViewById(R.id.btnAddItem);
        // מחבר את כפתור ההוספה מה-XML לקוד.

        btnAddItem.setEnabled(false);
        // בהתחלה הכפתור חסום עד שמוודאים שהמשתמש הוא מנהל.

        checkAdminAccess();
        // בודק האם המשתמש שמחובר הוא מנהל.

        btnAddItem.setOnClickListener(v -> addCourt());
        // כאשר לוחצים על הכפתור, מנסים להוסיף מגרש חדש.
    }

    private void addCourt() {
        // הפונקציה מוסיפה מגרש חדש ל-Firebase אחרי בדיקת קלט.

        if (!isAdminAllowed) {
            Toast.makeText(this, "אין לך הרשאה להוסיף מגרש", Toast.LENGTH_SHORT).show();
            return;
            // אם המשתמש לא מנהל, לא נותנים לו להוסיף מגרש.
        }

        String name = etItemName.getText().toString().trim();
        // לוקח את שם המגרש מהשדה ומסיר רווחים מיותרים.

        String info = etItemInfo.getText().toString().trim();
        // לוקח את המידע/כתובת של המגרש ומסיר רווחים מיותרים.

        String location = spLocation.getSelectedItem() != null
                ? spLocation.getSelectedItem().toString()
                : "";
        // לוקח את העיר שנבחרה ב-Spinner.
        // אם לא נבחר כלום, שם טקסט ריק.

        String type = spType.getSelectedItem() != null
                ? spType.getSelectedItem().toString()
                : "";
        // לוקח את סוג המגרש שנבחר.
        // אם לא נבחר כלום, שם טקסט ריק.

        if (name.isEmpty()) {
            etItemName.setError("Please enter a court name");
            return;
            // אם שם המגרש ריק, מציג שגיאה ועוצר.
        }

        if (info.isEmpty()) {
            etItemInfo.setError("Please enter court info");
            return;
            // אם המידע/כתובת ריקים, מציג שגיאה ועוצר.
        }

        if (location.equals("Choose city") || location.isEmpty()) {
            Toast.makeText(this, "Please choose a city", Toast.LENGTH_SHORT).show();
            return;
            // אם לא נבחרה עיר, עוצר ומציג הודעה.
        }

        if (type.equals("Choose type") || type.isEmpty()) {
            Toast.makeText(this, "Please choose a court type", Toast.LENGTH_SHORT).show();
            return;
            // אם לא נבחר סוג מגרש, עוצר ומציג הודעה.
        }

        DatabaseService db = DatabaseService.getInstance();
        // מקבל את השירות שאחראי על פעולות מול Firebase.

        String courtId = db.generateCourtId();
        // יוצר מזהה ייחודי למגרש החדש.

        Court court = new Court(courtId, name, location, info, type);
        // יוצר אובייקט חדש של מגרש עם כל הפרטים שהמנהל הכניס.

        db.createNewCourt(court, new DatabaseService.DatabaseCallback<Void>() {
            // שולח את המגרש לשמירה ב-Firebase.

            @Override
            public void onCompleted(Void object) {
                Toast.makeText(AddItem.this, "Court added successfully", Toast.LENGTH_SHORT).show();
                // אם המגרש נשמר בהצלחה, מציג הודעת הצלחה.

                finish();
                // סוגר את המסך וחוזר למסך הקודם.
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AddItem.this, "Failed to add court", Toast.LENGTH_SHORT).show();
                // אם השמירה נכשלה, מציג הודעת שגיאה.
            }
        });
    }

    private void checkAdminAccess() {
        // הפונקציה בודקת האם המשתמש שמחובר הוא מנהל.

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // מקבל את המשתמש שמחובר כרגע דרך Firebase Authentication.

        if (firebaseUser == null) {
            startActivity(new Intent(AddItem.this, Login.class));
            finish();
            return;
            // אם אין משתמש מחובר, מעבירים למסך Login וסוגרים את המסך הזה.
        }

        DatabaseService.getInstance().getUser(firebaseUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
            // מביא את פרטי המשתמש מה-Database לפי ה-uid שלו.

            @Override
            public void onCompleted(User user) {
                if (user == null || !user.isAdmin()) {
                    Toast.makeText(AddItem.this, "אין לך הרשאה להיכנס לעמוד מנהל", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddItem.this, UserPage.class));
                    finish();
                    return;
                    // אם המשתמש לא מנהל, מעבירים אותו למסך משתמש רגיל.
                }

                isAdminAllowed = true;
                // אם המשתמש מנהל, מסמנים שיש לו הרשאה.

                btnAddItem.setEnabled(true);
                // מאפשרים לו ללחוץ על כפתור ההוספה.
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AddItem.this, "שגיאה בבדיקת הרשאות", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddItem.this, UserPage.class));
                finish();
                // אם הייתה שגיאה בבדיקת ההרשאות, מחזירים למסך משתמש רגיל.
            }
        });
    }
}