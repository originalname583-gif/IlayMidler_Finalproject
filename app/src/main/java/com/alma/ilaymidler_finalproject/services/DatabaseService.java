package com.alma.ilaymidler_finalproject.services;

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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseService {

    private static final String USERS_PATH = "users";
    private static final String COURT_PATH = "courts";
    private static final String RESERVATIONS_PATH = "reservations";

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

    private void writeData(@NotNull final String path, @NotNull final Object data,
                           @Nullable final DatabaseCallback<Void> callback) {
        readData(path).setValue(data, (error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    public <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz,
                            @NotNull final DatabaseCallback<T> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz);
            callback.onCompleted(data);
        });
    }

    private <T> void getDataList(@NotNull final String path, @NotNull final Class<T> clazz,
                                 @NotNull final DatabaseCallback<List<T>> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
            }

            List<T> tList = new ArrayList<>();
            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                T t = dataSnapshot.getValue(clazz);
                if (t != null) tList.add(t);
            }
            callback.onCompleted(tList);
        });
    }

    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }

    public String generateCourtId() {
        return generateNewId(COURT_PATH);
    }

    public void createNewUser(@NotNull final User user, @Nullable final DatabaseCallback<Void> callback) {
        writeData(USERS_PATH + "/" + user.getId(), user, callback);
    }

    public void getUser(@NotNull String uid, @NotNull DatabaseCallback<User> callback) {
        getData(USERS_PATH + "/" + uid, User.class, callback);
    }

    public void updateUser(@NotNull final User user, @Nullable final DatabaseCallback<Void> callback) {
        writeData(USERS_PATH + "/" + user.getId(), user, callback);
    }

    public void getUserList(@NotNull final DatabaseCallback<List<User>> callback) {
        getDataList(USERS_PATH, User.class, callback);
    }

    public void deleteUser(String uid, DatabaseCallback<Void> callback) {
        readData(USERS_PATH + "/" + uid).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    public static void LoginUser(@NotNull final String email, final String password,
                                 @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                        ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                        : "";
                if (callback != null) callback.onCompleted(uid);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    public void createNewCourt(@NotNull final Court court, @Nullable final DatabaseCallback<Void> callback) {
        writeData(COURT_PATH + "/" + court.getId(), court, callback);
    }

    public void getCourt(@NotNull final String courtId, @NotNull final DatabaseCallback<Court> callback) {
        getData(COURT_PATH + "/" + courtId, Court.class, callback);
    }

    public void getCourtsList(@NotNull final DatabaseCallback<List<Court>> callback) {
        getDataList(COURT_PATH, Court.class, callback);
    }

    public void getUsers(@NotNull final DatabaseCallback<List<User>> callback) {
        getDataList(USERS_PATH, User.class, callback);
    }

    public void updateCourt(@NonNull Court court, @Nullable DatabaseCallback<Void> callback) {
        writeData(COURT_PATH + "/" + court.getId(), court, callback);
    }

    public void deleteCourt(@NonNull String courtId, @Nullable DatabaseCallback<Void> callback) {
        readData(COURT_PATH + "/" + courtId).removeValue((error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    public void setUserAdmin(@NonNull String userId, boolean isAdmin, @Nullable DatabaseCallback<Void> callback) {
        readData(USERS_PATH + "/" + userId + "/isAdmin").setValue(isAdmin, (error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    public static String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public static String getDatePlusDays(int days) {
        long time = System.currentTimeMillis() + (long) days * 24 * 60 * 60 * 1000;
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(time));
    }

    public void getReservationsForCourtOnDate(@NonNull String courtId, @NonNull String date,
                                              @NonNull DatabaseCallback<Map<String, Reservation>> callback) {
        readData(RESERVATIONS_PATH + "/" + date + "/" + courtId).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
            }

            Map<String, Reservation> result = new HashMap<>();
            for (DataSnapshot snap : task.getResult().getChildren()) {
                Reservation reservation = snap.getValue(Reservation.class);
                if (reservation != null) {
                    result.put(snap.getKey(), reservation);
                }
            }
            callback.onCompleted(result);
        });
    }

    public void getReservationsForUser(@NonNull String userId,
                                       @NonNull DatabaseCallback<List<Reservation>> callback) {

        readData(RESERVATIONS_PATH).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
            }

            List<Reservation> reservationList = new ArrayList<>();

            for (DataSnapshot dateSnap : task.getResult().getChildren()) {
                for (DataSnapshot courtSnap : dateSnap.getChildren()) {
                    for (DataSnapshot slotSnap : courtSnap.getChildren()) {
                        Reservation reservation = slotSnap.getValue(Reservation.class);
                        if (reservation != null && userId.equals(reservation.getUserId())) {
                            reservationList.add(reservation);
                        }
                    }
                }
            }

            callback.onCompleted(reservationList);
        });
    }

    public void cancelReservation(@NonNull Reservation reservation,
                                  @NonNull DatabaseCallback<Void> callback) {

        String path = RESERVATIONS_PATH + "/"
                + reservation.getBookingDate() + "/"
                + reservation.getCourtId() + "/"
                + reservation.getSlotId();

        readData(path).removeValue((error, ref) -> {
            if (error != null) {
                callback.onFailed(error.toException());
            } else {
                callback.onCompleted(null);
            }
        });
    }

    private boolean isOverlapping(String start1, String end1, String start2, String end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) return false;
        return start1.compareTo(end2) < 0 && start2.compareTo(end1) < 0;
    }

    public void reserveCourtSlot(@NonNull Court court,
                                 @NonNull String bookingDate,
                                 @NonNull String userId,
                                 @NonNull String userName,
                                 @NonNull String slotId,
                                 @NonNull String startTime,
                                 @NonNull String endTime,
                                 @NonNull DatabaseCallback<String> callback) {

        DatabaseReference dayRef = readData(RESERVATIONS_PATH + "/" + bookingDate);
        AtomicReference<String> failReason = new AtomicReference<>("");

        dayRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                int userReservationCount = 0;

                for (MutableData courtNode : currentData.getChildren()) {
                    for (MutableData slotNode : courtNode.getChildren()) {

                        Object value = slotNode.getValue();
                        if (!(value instanceof Map)) continue;

                        Map<?, ?> map = (Map<?, ?>) value;

                        String savedUserId = map.get("userId") != null ? map.get("userId").toString() : null;
                        String savedStart = map.get("startTime") != null ? map.get("startTime").toString() : null;
                        String savedEnd = map.get("endTime") != null ? map.get("endTime").toString() : null;

                        if (savedUserId == null) continue;

                        if (userId.equals(savedUserId)) {
                            userReservationCount++;

                            if (isOverlapping(savedStart, savedEnd, startTime, endTime)) {
                                failReason.set("You already have a reservation at this time.");
                                return Transaction.abort();
                            }
                        }
                    }
                }

                if (userReservationCount >= 2) {
                    failReason.set("You can reserve up to 2 time slots only.");
                    return Transaction.abort();
                }

                MutableData courtNode = currentData.child(court.getId());
                MutableData slotNode = courtNode.child(slotId);

                if (slotNode.getValue() != null) {
                    failReason.set("This slot is already reserved.");
                    return Transaction.abort();
                }

                String reservationId = readData(RESERVATIONS_PATH).push().getKey();

                Map<String, Object> reservationMap = new HashMap<>();
                reservationMap.put("id", reservationId);
                reservationMap.put("courtId", court.getId());
                reservationMap.put("userId", userId);
                reservationMap.put("userName", userName);
                reservationMap.put("slotId", slotId);
                reservationMap.put("startTime", startTime);
                reservationMap.put("endTime", endTime);
                reservationMap.put("bookingDate", bookingDate);
                reservationMap.put("createdAt", System.currentTimeMillis());

                slotNode.setValue(reservationMap);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                   @Nullable DataSnapshot currentData) {
                if (error != null) {
                    callback.onFailed(error.toException());
                    return;
                }

                if (!committed) {
                    callback.onFailed(new Exception(
                            failReason.get() == null || failReason.get().isEmpty()
                                    ? "Reservation failed."
                                    : failReason.get()
                    ));
                    return;
                }

                callback.onCompleted("Reservation successful!");
            }
        });
    }
}