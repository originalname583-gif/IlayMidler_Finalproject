package com.alma.ilaymidler_finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends BaseMenuActivity {

    private TextView tvFullName, tvEmail, tvPhone, tvRole, tvEmpty;
    private ProgressBar progressBar;

    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupToolbar(R.id.topToolbar, "My Profile");

        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvRole = findViewById(R.id.tvRole);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);

        databaseService = DatabaseService.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                progressBar.setVisibility(View.GONE);

                if (user == null) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Failed to load profile");
                    return;
                }

                String fullName = (user.getFirstName() + " " + user.getLastName()).trim();
                tvFullName.setText(fullName.isEmpty() ? "No name" : fullName);
                tvEmail.setText(user.getEmail().isEmpty() ? "No email" : user.getEmail());
                tvPhone.setText(user.getPhone().isEmpty() ? "No phone" : user.getPhone());
                tvRole.setText(user.isAdmin() ? "Admin" : "User");
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Failed to load profile");
                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}