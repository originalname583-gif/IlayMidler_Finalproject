package com.alma.ilaymidler_finalproject;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.alma.ilaymidler_finalproject.Model.User;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class ShowUsers extends AppCompatActivity {

    ListView listView;
    ArrayList<String> usersList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users);

        listView = findViewById(R.id.lvUsers);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usersList);
        listView.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    User u = s.getValue(User.class);
                    usersList.add(u.toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
