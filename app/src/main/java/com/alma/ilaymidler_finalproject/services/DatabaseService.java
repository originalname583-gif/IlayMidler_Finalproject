package com.alma.ilaymidler_finalproject.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.Model.Reservation;
import com.alma.ilaymidler_finalproject.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DatabaseService {

    private static final String TAG = "DatabaseService";
    private static final String USERS_PATH = "users",
                                COURT_PATH = "courts";

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




    /// get data from the database at a specific path
    /// @param path the path to get the data from
    /// @param clazz the class of the object to return
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseCallback
    /// @see Class
    public <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<T> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz);
            callback.onCompleted(data);
        });
    }

    /// get a list of data from the database at a specific path
    /// @param path the path to get the data from
    /// @param clazz the class of the objects to return
    /// @param callback the callback to call when the operation is completed
    private <T> void getDataList(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<List<T>> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<T> tList = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                T t = dataSnapshot.getValue(clazz);
                tList.add(t);
            });

            callback.onCompleted(tList);
        });
    }

    /// generate a new id for a new object in the database
    /// @param path the path to generate the id for
    /// @return a new id for the object
    /// @see String
    /// @see DatabaseReference#push()









    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }

    // ===================== USER SECTION =====================

  
    public void createNewUser(@NotNull final User user, @Nullable final DatabaseCallback<Void> callback) {
        writeData(USERS_PATH + "/" + user.getId(), user, callback);
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
    public static void LoginUser(@NotNull final String email, final String password,
                                 @Nullable final DatabaseCallback<String> callback) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.d("TAG", "createUserWithEmail:success");

                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        callback.onCompleted(uid);

                    } else {
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());

                        if (callback != null)
                            callback.onFailed(task.getException());
                    }
                });
    }



/// generate a new id for a new court in the database
/// @return a new id for the court
/// @see #generateNewId(String)
/// @see Court
public String generateCourtId() {
    return generateNewId(COURT_PATH);
}



/// create a new court in the database
/// @param court the court object to create
/// @param callback the callback to call when the operation is completed
///              the callback will receive void
///             if the operation fails, the callback will receive an exception
/// @return void
/// @see DatabaseCallback
/// @see Court
public void createNewCourt(@NotNull final Court court, @Nullable final DatabaseService.DatabaseCallback<Void> callback) {
    writeData("COURT_PATH/" + court.getId(), court, callback);
}



public void updateUser(@NotNull final User user ,@Nullable final DatabaseService.DatabaseCallback<Void> callback) {
    writeData("Users/" + user.getId(), user, callback);
}





/// get a court from the database
/// @param courtId the id of the court to get
/// @param callback the callback to call when the operation is completed
///               the callback will receive the court object
///              if the operation fails, the callback will receive an exception
/// @return void
/// @see DatabaseCallback
/// @see Court
public void getCourt(@NotNull final String courtId, @NotNull final DatabaseCallback<Court> callback) {
    getData("COURT_PATH"+ "/"+ courtId, Court.class,callback);
}




/// get all the courts from the database
/// @param callback the callback to call when the operation is completed
///              the callback will receive a list of court objects
///            if the operation fails, the callback will receive an exception
/// @return void
/// @see DatabaseCallback
/// @see List
/// @see Court
/// @see #getData(String, Class, DatabaseCallback)
public void getCourtsList(@NotNull final DatabaseCallback<List<Court>> callback) {
    getDataList(COURT_PATH, Court.class, callback);
}



/// get all the users from the database
/// @param callback the callback to call when the operation is completed
///              the callback will receive a list of court objects
///            if the operation fails, the callback will receive an exception
/// @return void
/// @see DatabaseCallback
/// @see List
/// @see Court
/// @see #getData(String, Class, DatabaseCallback)
public void getUsers(@NotNull final DatabaseService.DatabaseCallback<List<User>> callback) {
    getDataList(USERS_PATH, User.class, callback);
}

public void deleteUser(String uid, DatabaseService.DatabaseCallback<Void> callback) {
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
    userRef.removeValue().addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            callback.onCompleted(null);
        } else {
            callback.onFailed(task.getException());
        }
    });
}

public void saveReservation(Reservation reservation, DatabaseService.DatabaseCallback<Void> callback) {
    DatabaseReference ref = FirebaseDatabase.getInstance()
            .getReference("reservations")
            .child(reservation.getId());

    ref.setValue(reservation)
            .addOnSuccessListener(aVoid -> callback.onCompleted(null))
            .addOnFailureListener(callback::onFailed);
}

public void getReservation(String reservationId, DatabaseService.DatabaseCallback<Reservation> callback) {
    getData("reservations/"+reservationId, Reservation.class, callback);
}


public void getReservations(final DatabaseCallback<List<Reservation>> callback) {
    getDataList("reservations", Reservation.class,  callback);
}


}

