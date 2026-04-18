package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminPage extends BaseMenuActivity {

    Button btnAddCourt, btnShowUsers, btnManageCourts, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        setupToolbar(R.id.topToolbar, "Admin Dashboard");

        btnAddCourt = findViewById(R.id.btnAddCourt);
        btnShowUsers = findViewById(R.id.btnShowUsers);
        btnManageCourts = findViewById(R.id.btnManageCourts);
        btnLogout = findViewById(R.id.btnLogout);

        btnAddCourt.setOnClickListener(v ->
                startActivity(new Intent(AdminPage.this, AddItem.class)));

        btnShowUsers.setOnClickListener(v ->
                startActivity(new Intent(AdminPage.this, ShowUsers.class)));

        btnManageCourts.setOnClickListener(v ->
                startActivity(new Intent(AdminPage.this, ManageCourtsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}