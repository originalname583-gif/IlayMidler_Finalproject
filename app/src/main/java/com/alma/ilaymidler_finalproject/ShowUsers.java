package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.adapters.UserAdapter;
import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.services.DatabaseService;

import java.util.List;

public class ShowUsers extends AppCompatActivity {

    private static final String TAG = "ShowUsers";

    private UserAdapter userAdapter;
    private TextView tvUserCount;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users);

        databaseService = DatabaseService.getInstance();

        RecyclerView rvUsers = findViewById(R.id.rv_users);
        tvUserCount = findViewById(R.id.tv_user_count);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                Intent intent = new Intent(ShowUsers.this, UserPage.class);
                intent.putExtra("USER_UID", user.getId());
                startActivity(intent);
            }

            @Override
            public void onLongUserClick(User user) {
                Log.d(TAG, "Long click: " + user.getEmail());
            }
        });

        rvUsers.setAdapter(userAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
}
