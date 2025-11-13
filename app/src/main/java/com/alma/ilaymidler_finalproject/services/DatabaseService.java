package com.alma.ilaymidler_finalproject.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alma.ilaymidler_finalproject.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseService {

    private static final String TAG = "DatabaseService";
    private static final String USERS_PATH = "users";

    public interface DatabaseCallback<T> {
        void onCompleted(T object);
        void onFailed(Exception e);
    }

    private static DatabaseService instance;
    private final DatabaseReference databaseReference;

    private DatabaseService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);
    }

    private void writeData(@NotNull final String path, @NotNull final Object data, final @Nullable DatabaseCallback<Void> callback) {
        readData(path).setValue(data, (error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }

    // ===================== USER SECTION =====================

    public String generateUserId() {
        return generateNewId(USERS_PATH);
    }

    public void createNewUser(@NotNull final User user, @Nullable final DatabaseCallback<Void> callback) {
        writeData(USERS_PATH + "/" + user.getId(), user, callback);
    }

    public void checkIfEmailExists(@NotNull final String gmail, @NotNull final DatabaseCallback<Boolean> callback) {
        // FIXED: checking "gmail" field instead of "email"
        readData(USERS_PATH).orderByChild("gmail").equalTo(gmail).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Error getting data", task.getException());
                        callback.onFailed(task.getException());
                        return;
                    }
                    boolean exists = task.getResult().getChildrenCount() > 0;
                    callback.onCompleted(exists);
                });
    }

    public void getUserByEmailAndPassword(@NotNull final String gmail, @NotNull final String password, @NotNull final DatabaseCallback<User> callback) {
        readData(USERS_PATH).orderByChild("gmail").equalTo(gmail).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Error getting data", task.getException());
                        callback.onFailed(task.getException());
                        return;
                    }
                    if (task.getResult().getChildrenCount() == 0) {
                        callback.onFailed(new Exception("User not found"));
                        return;
                    }
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null || !Objects.equals(user.getPassword(), password)) {
                            callback.onFailed(new Exception("Invalid email or password"));
                            return;
                        }
                        callback.onCompleted(user);
                        return;
                    }
                });
    }

    public void getUserList(@NotNull final DatabaseCallback<List<User>> callback) {
        readData(USERS_PATH).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<User> users = new ArrayList<>();
            task.getResult().getChildren().forEach(snapshot -> {
                User user = snapshot.getValue(User.class);
                if (user != null) users.add(user);
            });
            callback.onCompleted(users);
        });
    }
}
