package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.alma.ilaymidler_finalproject.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseMenuActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    // המשתנה שומר את ה-Toolbar של המסך.

    protected boolean isAdminUser = false;
    // שומר האם המשתמש הנוכחי הוא מנהל.

    protected boolean isLoggedIn = false;
    // שומר האם יש משתמש מחובר.

    private boolean adminLoaded = false;
    // שומר האם סיימנו לבדוק אם המשתמש מנהל.

    protected void setupToolbar(int toolbarId, String title) {
        // הפונקציה מחברת את ה-Toolbar למסך ומפעילה אותו.

        toolbar = findViewById(toolbarId);
        // מוצא את ה-Toolbar מתוך ה-XML.

        if (toolbar != null) {

            toolbar.setTitle(title);
            // קובע את הכותרת של המסך.

            setSupportActionBar(toolbar);
            // הופך את ה-Toolbar לתפריט הראשי של המסך.
        }

        loadUserRole();
        // בודק האם המשתמש הוא מנהל.
    }

    private void loadUserRole() {
        // הפונקציה בודקת האם יש משתמש מחובר והאם הוא מנהל.

        FirebaseUser firebaseUser =
                FirebaseAuth.getInstance().getCurrentUser();
        // לוקח את המשתמש המחובר.

        isLoggedIn = firebaseUser != null;
        // בודק האם יש משתמש מחובר.

        if (!isLoggedIn) {

            isAdminUser = false;
            // אם אין משתמש מחובר הוא לא מנהל.

            adminLoaded = true;
            // סימון שסיימנו את הבדיקה.

            invalidateOptionsMenu();
            // מרענן את התפריט.

            return;
        }

        DatabaseService.getInstance().getUser(
                firebaseUser.getUid(),
                new DatabaseService.DatabaseCallback<User>() {

                    @Override
                    public void onCompleted(User user) {

                        isAdminUser =
                                user != null &&
                                        user.isAdmin();
                        // בודק האם המשתמש מנהל.

                        adminLoaded = true;
                        // סימון שסיימנו לבדוק.

                        invalidateOptionsMenu();
                        // מרענן את התפריט.
                    }

                    @Override
                    public void onFailed(Exception e) {

                        isAdminUser = false;
                        // במקרה של שגיאה המשתמש לא ייחשב מנהל.

                        adminLoaded = true;
                        // סימון שסיימנו לבדוק.

                        invalidateOptionsMenu();
                        // מרענן את התפריט.
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // הפונקציה יוצרת את התפריט העליון.

        getMenuInflater().inflate(
                R.menu.app_top_menu,
                menu
        );
        // טוענת את קובץ התפריט.

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // הפונקציה מחליטה אילו כפתורים יוצגו בתפריט.

        MenuItem itemLogin =
                menu.findItem(R.id.menu_login);

        MenuItem itemRegister =
                menu.findItem(R.id.menu_register);

        MenuItem itemUserHome =
                menu.findItem(R.id.menu_user_home);

        MenuItem itemMyReservations =
                menu.findItem(R.id.menu_my_reservations);

        MenuItem itemProfile =
                menu.findItem(R.id.menu_profile);

        MenuItem itemAdminHome =
                menu.findItem(R.id.menu_admin_home);

        MenuItem itemAddCourt =
                menu.findItem(R.id.menu_add_court);

        MenuItem itemShowUsers =
                menu.findItem(R.id.menu_show_users);

        MenuItem itemManageCourts =
                menu.findItem(R.id.menu_manage_courts);

        MenuItem itemLogout =
                menu.findItem(R.id.menu_logout);

        isLoggedIn =
                FirebaseAuth.getInstance()
                        .getCurrentUser() != null;
        // בודק האם יש משתמש מחובר.

        if (itemLogin != null)
            itemLogin.setVisible(!isLoggedIn);
        // Login מוצג רק אם אין משתמש מחובר.

        if (itemRegister != null)
            itemRegister.setVisible(!isLoggedIn);
        // Register מוצג רק אם אין משתמש מחובר.

        if (itemUserHome != null)
            itemUserHome.setVisible(isLoggedIn);
        // דף משתמש מוצג רק למשתמש מחובר.

        if (itemMyReservations != null)
            itemMyReservations.setVisible(isLoggedIn);
        // ההזמנות שלי מוצג רק למשתמש מחובר.

        if (itemProfile != null)
            itemProfile.setVisible(isLoggedIn);
        // פרופיל מוצג רק למשתמש מחובר.

        if (itemLogout != null)
            itemLogout.setVisible(isLoggedIn);
        // Logout מוצג רק למשתמש מחובר.

        boolean canSeeAdmin =
                isLoggedIn &&
                        adminLoaded &&
                        isAdminUser;
        // רק מנהל מחובר יכול לראות תפריטי מנהל.

        if (itemAdminHome != null)
            itemAdminHome.setVisible(canSeeAdmin);

        if (itemAddCourt != null)
            itemAddCourt.setVisible(canSeeAdmin);

        if (itemShowUsers != null)
            itemShowUsers.setVisible(canSeeAdmin);

        if (itemManageCourts != null)
            itemManageCourts.setVisible(canSeeAdmin);

        return true;
    }

    private boolean blockIfNotAdmin() {
        // הפונקציה חוסמת משתמש רגיל מגישה למסכי מנהל.

        if (!isLoggedIn || !isAdminUser) {

            Toast.makeText(
                    this,
                    "אין לך הרשאה להיכנס לעמוד מנהל",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(
                    new Intent(
                            this,
                            UserPage.class
                    )
            );
            // מעביר את המשתמש לדף המשתמש.

            return true;
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // מופעל כאשר המשתמש לוחץ על כפתור בתפריט.

        int id = item.getItemId();

        if (id == R.id.menu_main) {

            startActivity(
                    new Intent(
                            this,
                            MainActivity.class
                    )
            );

            return true;
        }

        if (id == R.id.menu_about) {

            startActivity(
                    new Intent(
                            this,
                            About.class
                    )
            );

            return true;
        }

        if (id == R.id.menu_login) {

            startActivity(
                    new Intent(
                            this,
                            Login.class
                    )
            );

            return true;
        }

        if (id == R.id.menu_register) {

            startActivity(
                    new Intent(
                            this,
                            Register.class
                    )
            );

            return true;
        }

        if (id == R.id.menu_user_home) {

            startActivity(
                    new Intent(
                            this,
                            UserPage.class
                    )
            );

            return true;
        }

        if (id == R.id.menu_my_reservations) {

            startActivity(
                    new Intent(
                            this,
                            MyReservationsActivity.class
                    )
            );

            return true;
        }

        if (id == R.id.menu_profile) {

            startActivity(
                    new Intent(
                            this,
                            ProfileActivity.class
                    )
            );

            return true;
        }

        if (id == R.id.menu_admin_home) {

            if (blockIfNotAdmin())
                return true;
            // בודק שהמשתמש מנהל.

            startActivity(
                    new Intent(
                            this,
                            AdminPage.class
                    )
            );

            return true;
        }

        if (id == R.id.menu_add_court) {

            if (blockIfNotAdmin())
                return true;

            startActivity(
                    new Intent(
                            this,
                            AddItem.class
                    )
            );

            return true;
        }

        if (id == R.id.menu_show_users) {

            if (blockIfNotAdmin())
                return true;

            startActivity(
                    new Intent(
                            this,
                            ShowUsers.class
                    )
            );

            return true;
        }

        if (id == R.id.menu_manage_courts) {

            if (blockIfNotAdmin())
                return true;

            startActivity(
                    new Intent(
                            this,
                            ManageCourtsActivity.class
                    )
            );

            return true;
        }

        if (id == R.id.menu_logout) {
            // מופעל כאשר המשתמש לוחץ Logout.

            FirebaseAuth.getInstance().signOut();
            // מנתק את המשתמש מ-Firebase.

            SessionManager.clearSession(this);
            // מוחק את כל המידע שנשמר ב-SharedPreferences.
            // זה התיקון החשוב שחסר אצלך.

            Intent intent =
                    new Intent(
                            this,
                            MainActivity.class
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
            );
            // מנקה את כל המסכים הפתוחים.

            startActivity(intent);
            // חוזר למסך הראשי.

            finish();
            // סוגר את המסך הנוכחי.

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}