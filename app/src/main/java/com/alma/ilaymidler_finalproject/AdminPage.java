package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminPage extends AppCompatActivity {

    Button btnAddCourt, btnShowUsers, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        btnAddCourt = findViewById(R.id.btnAddCourt);
        btnShowUsers = findViewById(R.id.btnShowUsers);
        btnLogout = findViewById(R.id.btnLogout);

        btnAddCourt.setOnClickListener(v ->
                startActivity(new Intent(AdminPage.this, AddItem.class)));

        btnShowUsers.setOnClickListener(v ->
                startActivity(new Intent(AdminPage.this, ShowUsers.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}