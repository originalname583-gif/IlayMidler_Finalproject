package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

public abstract class BaseMenuActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected boolean isAdminUser = false;
    protected boolean isLoggedIn = false;

    protected void setupToolbar(int toolbarId, String title) {
        toolbar = findViewById(toolbarId);
        if (toolbar != null) {
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_top_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemLogin = menu.findItem(R.id.menu_login);
        MenuItem itemRegister = menu.findItem(R.id.menu_register);
        MenuItem itemUserHome = menu.findItem(R.id.menu_user_home);
        MenuItem itemMyReservations = menu.findItem(R.id.menu_my_reservations);
        MenuItem itemProfile = menu.findItem(R.id.menu_profile);
        MenuItem itemAdminHome = menu.findItem(R.id.menu_admin_home);
        MenuItem itemAddCourt = menu.findItem(R.id.menu_add_court);
        MenuItem itemShowUsers = menu.findItem(R.id.menu_show_users);
        MenuItem itemLogout = menu.findItem(R.id.menu_logout);
        MenuItem itemManageCourts = menu.findItem(R.id.menu_manage_courts);
        isLoggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;

        if (isLoggedIn) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseService.getInstance().getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    isAdminUser = user != null && user.isAdmin();
                    invalidateOptionsMenu();
                    if (itemLogin != null) itemLogin.setVisible(!isLoggedIn);
                    if (itemRegister != null) itemRegister.setVisible(!isLoggedIn);
                    if (itemManageCourts != null) itemManageCourts.setVisible(isLoggedIn && isAdminUser);

                    if (itemUserHome != null) itemUserHome.setVisible(isLoggedIn);
                    if (itemMyReservations != null) itemMyReservations.setVisible(isLoggedIn);
                    if (itemProfile != null) itemProfile.setVisible(isLoggedIn);
                    if (itemLogout != null) itemLogout.setVisible(isLoggedIn);

                    if (itemAdminHome != null) itemAdminHome.setVisible(isLoggedIn && isAdminUser);
                    if (itemAddCourt != null) itemAddCourt.setVisible(isLoggedIn && isAdminUser);
                    if (itemShowUsers != null) itemShowUsers.setVisible(isLoggedIn && isAdminUser);
                }

                @Override
                public void onFailed(Exception e) {
                    isAdminUser = false;
                    invalidateOptionsMenu();
                }
            });
        } else {
            isAdminUser = false;
            invalidateOptionsMenu();
        }



        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_main) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        if (id == R.id.menu_my_reservations) {
            startActivity(new Intent(this, MyReservationsActivity.class));
            return true;
        }
        if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }

        if (id == R.id.menu_about) {
            startActivity(new Intent(this, About.class));
            return true;
        }
        if (id == R.id.menu_manage_courts ) {
            startActivity(new Intent(this, ManageCourtsActivity.class));
            return true;
        }

        if (id == R.id.menu_login) {
            startActivity(new Intent(this, Login.class));
            return true;
        }

        if (id == R.id.menu_register) {
            startActivity(new Intent(this, Register.class));
            return true;
        }

        if (id == R.id.menu_user_home) {
            startActivity(new Intent(this, UserPage.class));
            return true;
        }

        if (id == R.id.menu_admin_home ) {


            startActivity(new Intent(this, AdminPage.class));
            return true;
        }

        if (id == R.id.menu_add_court ) {
            startActivity(new Intent(this, AddItem.class));
            return true;
        }

        if (id == R.id.menu_show_users) {
            startActivity(new Intent(this, ShowUsers.class));
            return true;
        }

        if (id == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}