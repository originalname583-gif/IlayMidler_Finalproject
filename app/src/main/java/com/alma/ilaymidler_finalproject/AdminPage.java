package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminPage extends AppCompatActivity {

    Button btnAddCourt, btnShowUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        btnAddCourt = findViewById(R.id.btnAddCourt);
        btnShowUsers = findViewById(R.id.btnShowUsers);

        btnAddCourt.setOnClickListener(v ->
                startActivity(new Intent(AdminPage.this, AddItem.class)));

        btnShowUsers.setOnClickListener(v ->
                startActivity(new Intent(AdminPage.this, ShowUsers.class)));
    }
}
