package com.alma.ilaymidler_finalproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.adapters.UserAdapter;
import com.alma.ilaymidler_finalproject.services.DatabaseService;

import java.util.List;

public class ShowUsers extends BaseMenuActivity {

    private static final String TAG = "ShowUsers";

    private UserAdapter userAdapter;
    private TextView tvUserCount;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users);

        setupToolbar(R.id.topToolbar, "Users");

        databaseService = DatabaseService.getInstance();

        RecyclerView rvUsers = findViewById(R.id.rv_users);
        tvUserCount = findViewById(R.id.tv_user_count);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                Toast.makeText(ShowUsers.this,
                        user.isAdmin() ? "Admin user" : "Regular user",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongUserClick(User user) {
                showAdminDialog(user);
            }
        });

        rvUsers.setAdapter(userAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        databaseService.getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> users) {
                userAdapter.setUserList(users);
                tvUserCount.setText("Total users: " + users.size());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed loading users", e);
            }
        });
    }

    private void showAdminDialog(User user) {
        String action = user.isAdmin() ? "Remove admin access" : "Make admin";

        new AlertDialog.Builder(this)
                .setTitle(user.getFirstName() + " " + user.getLastName())
                .setMessage(action + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    databaseService.setUserAdmin(user.getId(), !user.isAdmin(),
                            new DatabaseService.DatabaseCallback<Void>() {
                                @Override
                                public void onCompleted(Void object) {
                                    Toast.makeText(ShowUsers.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                    loadUsers();
                                }

                                @Override
                                public void onFailed(Exception e) {
                                    Toast.makeText(ShowUsers.this, "Failed to update admin", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }
}