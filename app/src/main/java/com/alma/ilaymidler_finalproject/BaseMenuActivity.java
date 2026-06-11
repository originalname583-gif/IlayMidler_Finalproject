package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseMenuActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    // משתנה שמייצג את סרגל הכלים העליון של המסך.

    protected boolean isAdminUser = false;
    // שומר האם המשתמש המחובר הוא מנהל.

    protected boolean isLoggedIn = false;
    // שומר האם יש משתמש מחובר כרגע.

    private boolean adminLoaded = false;
    // שומר האם סיימנו לבדוק אם המשתמש מנהל או לא.

    protected void setupToolbar(int toolbarId, String title) {
        // הפונקציה מגדירה את ה-Toolbar של המסך.

        toolbar = findViewById(toolbarId);
        // מחבר את ה-Toolbar מה-XML לקוד.

        if (toolbar != null) {
            toolbar.setTitle(title);
            // שם כותרת בסרגל העליון.

            setSupportActionBar(toolbar);
            // מגדיר את ה-Toolbar כ-ActionBar של המסך.
        }

        loadUserRole();
        // טוען את תפקיד המשתמש כדי לדעת איזה תפריט להציג.
    }

    private void loadUserRole() {
        // הפונקציה בודקת אם המשתמש מחובר ואם הוא מנהל.

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // מקבל את המשתמש שמחובר כרגע דרך Firebase Authentication.

        isLoggedIn = firebaseUser != null;
        // אם המשתמש לא null סימן שיש משתמש מחובר.

        if (!isLoggedIn) {
            isAdminUser = false;
            // אם אין משתמש מחובר, הוא בטוח לא מנהל.

            adminLoaded = true;
            // מסמנים שסיימנו לבדוק הרשאות.

            invalidateOptionsMenu();
            // מרענן את התפריט העליון.

            return;
            // עוצר את המשך הפונקציה.
        }

        DatabaseService.getInstance().getUser(firebaseUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
            // מביא את פרטי המשתמש מה-Database לפי ה-uid שלו.

            @Override
            public void onCompleted(User user) {
                isAdminUser = user != null && user.isAdmin();
                // אם המשתמש קיים והוא מנהל, שומרים true.

                adminLoaded = true;
                // מסמנים שסיימנו לבדוק האם הוא מנהל.

                invalidateOptionsMenu();
                // מרענן את התפריט כדי להציג/להסתיר אפשרויות.
            }

            @Override
            public void onFailed(Exception e) {
                isAdminUser = false;
                // אם הייתה שגיאה, לא נותנים הרשאת מנהל.

                adminLoaded = true;
                // מסמנים שסיימנו לבדוק.

                invalidateOptionsMenu();
                // מרענן את התפריט.
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // הפונקציה יוצרת את התפריט העליון.

        getMenuInflater().inflate(R.menu.app_top_menu, menu);
        // טוען את קובץ התפריט app_top_menu.xml.

        return true;
        // מחזיר true כדי שהתפריט יוצג.
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // הפונקציה קובעת אילו כפתורים בתפריט יוצגו ואילו יוסתרו.

        MenuItem itemLogin = menu.findItem(R.id.menu_login);
        // מוצא את כפתור Login בתפריט.

        MenuItem itemRegister = menu.findItem(R.id.menu_register);
        // מוצא את כפתור Register בתפריט.

        MenuItem itemUserHome = menu.findItem(R.id.menu_user_home);
        // מוצא את כפתור עמוד המשתמש.

        MenuItem itemMyReservations = menu.findItem(R.id.menu_my_reservations);
        // מוצא את כפתור ההזמנות שלי.

        MenuItem itemProfile = menu.findItem(R.id.menu_profile);
        // מוצא את כפתור הפרופיל.

        MenuItem itemAdminHome = menu.findItem(R.id.menu_admin_home);
        // מוצא את כפתור עמוד המנהל.

        MenuItem itemAddCourt = menu.findItem(R.id.menu_add_court);
        // מוצא את כפתור הוספת מגרש.

        MenuItem itemShowUsers = menu.findItem(R.id.menu_show_users);
        // מוצא את כפתור הצגת המשתמשים.

        MenuItem itemLogout = menu.findItem(R.id.menu_logout);
        // מוצא את כפתור ההתנתקות.

        MenuItem itemManageCourts = menu.findItem(R.id.menu_manage_courts);
        // מוצא את כפתור ניהול המגרשים.

        isLoggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;
        // בודק שוב האם יש משתמש מחובר.

        if (itemLogin != null) itemLogin.setVisible(!isLoggedIn);
        // מציג Login רק אם אין משתמש מחובר.

        if (itemRegister != null) itemRegister.setVisible(!isLoggedIn);
        // מציג Register רק אם אין משתמש מחובר.

        if (itemUserHome != null) itemUserHome.setVisible(isLoggedIn);
        // מציג עמוד משתמש רק אם יש משתמש מחובר.

        if (itemMyReservations != null) itemMyReservations.setVisible(isLoggedIn);
        // מציג ההזמנות שלי רק אם יש משתמש מחובר.

        if (itemProfile != null) itemProfile.setVisible(isLoggedIn);
        // מציג פרופיל רק אם יש משתמש מחובר.

        if (itemLogout != null) itemLogout.setVisible(isLoggedIn);
        // מציג Logout רק אם יש משתמש מחובר.

        boolean canSeeAdmin = isLoggedIn && adminLoaded && isAdminUser;
        // קובע האם מותר להציג אפשרויות מנהל.
        // צריך להיות מחובר, טעינת הרשאה הסתיימה, והמשתמש מנהל.

        if (itemAdminHome != null) itemAdminHome.setVisible(canSeeAdmin);
        // מציג עמוד מנהל רק למנהל.

        if (itemAddCourt != null) itemAddCourt.setVisible(canSeeAdmin);
        // מציג הוספת מגרש רק למנהל.

        if (itemShowUsers != null) itemShowUsers.setVisible(canSeeAdmin);
        // מציג רשימת משתמשים רק למנהל.

        if (itemManageCourts != null) itemManageCourts.setVisible(canSeeAdmin);
        // מציג ניהול מגרשים רק למנהל.

        return true;
        // מחזיר true כדי שהתפריט יתעדכן.
    }

    private boolean blockIfNotAdmin() {
        // הפונקציה חוסמת כניסה לעמודי מנהל אם המשתמש לא מנהל.

        if (!isLoggedIn || !adminLoaded || !isAdminUser) {
            // אם המשתמש לא מחובר, או שההרשאה עוד לא נטענה, או שהוא לא מנהל.

            Toast.makeText(this, "אין לך הרשאה להיכנס לעמוד מנהל", Toast.LENGTH_SHORT).show();
            // מציג הודעה שאין הרשאה.

            startActivity(new Intent(this, UserPage.class));
            // מעביר את המשתמש לעמוד משתמש רגיל.

            return true;
            // מחזיר true כדי להגיד שהכניסה נחסמה.
        }

        return false;
        // מחזיר false אם המשתמש כן מנהל ואפשר לתת לו להיכנס.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // הפונקציה מופעלת כאשר המשתמש לוחץ על פריט בתפריט.

        int id = item.getItemId();
        // לוקח את ה-id של הפריט שנלחץ.

        if (id == R.id.menu_main) {
            startActivity(new Intent(this, MainActivity.class));
            // מעבר למסך הראשי.

            return true;
        }

        if (id == R.id.menu_about) {
            startActivity(new Intent(this, About.class));
            // מעבר למסך About.

            return true;
        }

        if (id == R.id.menu_login) {
            startActivity(new Intent(this, Login.class));
            // מעבר למסך התחברות.

            return true;
        }

        if (id == R.id.menu_register) {
            startActivity(new Intent(this, Register.class));
            // מעבר למסך הרשמה.

            return true;
        }

        if (id == R.id.menu_user_home) {
            startActivity(new Intent(this, UserPage.class));
            // מעבר לעמוד המשתמש.

            return true;
        }

        if (id == R.id.menu_my_reservations) {
            startActivity(new Intent(this, MyReservationsActivity.class));
            // מעבר למסך ההזמנות שלי.

            return true;
        }

        if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            // מעבר למסך פרופיל.

            return true;
        }

        if (id == R.id.menu_admin_home) {
            if (blockIfNotAdmin()) return true;
            // בודק הרשאת מנהל לפני כניסה.

            startActivity(new Intent(this, AdminPage.class));
            // מעבר לעמוד המנהל.

            return true;
        }

        if (id == R.id.menu_add_court) {
            if (blockIfNotAdmin()) return true;
            // בודק הרשאת מנהל לפני הוספת מגרש.

            startActivity(new Intent(this, AddItem.class));
            // מעבר למסך הוספת מגרש.

            return true;
        }

        if (id == R.id.menu_show_users) {
            if (blockIfNotAdmin()) return true;
            // בודק הרשאת מנהל לפני הצגת משתמשים.

            startActivity(new Intent(this, ShowUsers.class));
            // מעבר למסך רשימת משתמשים.

            return true;
        }

        if (id == R.id.menu_manage_courts) {
            if (blockIfNotAdmin()) return true;
            // בודק הרשאת מנהל לפני ניהול מגרשים.

            startActivity(new Intent(this, ManageCourtsActivity.class));
            // מעבר למסך ניהול מגרשים.

            return true;
        }

        if (id == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            // מנתק את המשתמש מ-Firebase.

            Intent intent = new Intent(this, MainActivity.class);
            // יוצר מעבר למסך הראשי.

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // מנקה את היסטוריית המסכים כדי שלא יחזור אחורה לעמודים מוגנים.

            startActivity(intent);
            // פותח את המסך הראשי.

            finish();
            // סוגר את המסך הנוכחי.

            return true;
        }

        return super.onOptionsItemSelected(item);
        // אם לא נמצא פריט מתאים, נותן למחלקת האב לטפל בזה.
    }
}