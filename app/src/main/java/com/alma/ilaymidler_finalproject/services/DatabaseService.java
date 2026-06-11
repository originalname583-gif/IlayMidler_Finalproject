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
    // הנתיב שבו נשמרים המשתמשים ב-Firebase.

    private static final String COURT_PATH = "courts";
    // הנתיב שבו נשמרים המגרשים ב-Firebase.

    private static final String RESERVATIONS_PATH = "reservations";
    // הנתיב שבו נשמרות ההזמנות ב-Firebase.

    public interface DatabaseCallback<T> {
        void onCompleted(T object);
        // מופעל כשהפעולה הצליחה.

        void onFailed(Exception e);
        // מופעל כשהפעולה נכשלה.
    }

    private static DatabaseService instance;
    // שומר מופע אחד של המחלקה לכל האפליקציה.

    private final DatabaseReference databaseReference;
    // החיבור הראשי למסד הנתונים.

    private DatabaseService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // מקבל את Firebase Database.

        databaseReference = firebaseDatabase.getReference();
        // מקבל את הנתיב הראשי של המסד.
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
            // אם עדיין אין שירות, יוצרים אחד חדש.
        }

        return instance;
        // מחזיר את אותו השירות לכל האפליקציה.
    }

    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);
        // מחזיר הפניה לנתיב מסוים ב-Firebase.
    }

    private void writeData(@NotNull final String path,
                           @NotNull final Object data,
                           @Nullable final DatabaseCallback<Void> callback) {

        readData(path).setValue(data, (error, ref) -> {
            // שומר את המידע בנתיב שקיבלנו.

            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
                // אם יש שגיאה, מחזירים אותה.
            } else {
                if (callback != null) callback.onCompleted(null);
                // אם הצליח, מחזירים הצלחה.
            }
        });
    }

    public <T> void getData(@NotNull final String path,
                            @NotNull final Class<T> clazz,
                            @NotNull final DatabaseCallback<T> callback) {

        readData(path).get().addOnCompleteListener(task -> {
            // קורא מידע מנתיב מסוים ב-Firebase.

            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
                // אם הקריאה נכשלה, מחזירים שגיאה.
            }

            T data = task.getResult().getValue(clazz);
            // ממיר את הנתון לאובייקט מהסוג שקיבלנו.

            callback.onCompleted(data);
            // מחזיר את הנתון למסך שביקש אותו.
        });
    }

    private <T> void getDataList(@NotNull final String path,
                                 @NotNull final Class<T> clazz,
                                 @NotNull final DatabaseCallback<List<T>> callback) {

        readData(path).get().addOnCompleteListener(task -> {
            // קורא רשימה של נתונים מ-Firebase.

            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
                // אם הקריאה נכשלה, מחזירים שגיאה.
            }

            List<T> tList = new ArrayList<>();
            // יוצר רשימה ריקה שאליה נוסיף את הנתונים.

            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                // עובר על כל הנתונים שנמצאים בנתיב.

                T t = dataSnapshot.getValue(clazz);
                // ממיר כל נתון לאובייקט Java.

                if (t != null) {
                    tList.add(t);
                    // מוסיף לרשימה רק אם הנתון לא ריק.
                }
            }

            callback.onCompleted(tList);
            // מחזיר את הרשימה למסך שביקש אותה.
        });
    }

    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
        // יוצר id חדש וייחודי ב-Firebase.
    }

    public String generateCourtId() {
        return generateNewId(COURT_PATH);
        // יוצר id חדש למגרש.
    }

    public void createNewUser(@NotNull final User user,
                              @Nullable final DatabaseCallback<Void> callback) {

        writeData(USERS_PATH + "/" + user.getId(), user, callback);
        // שומר משתמש חדש לפי ה-id שלו.
    }

    public void getUser(@NotNull String uid,
                        @NotNull DatabaseCallback<User> callback) {

        getData(USERS_PATH + "/" + uid, User.class, callback);
        // מביא משתמש אחד לפי ה-uid שלו.
    }

    public void updateUser(@NotNull final User user,
                           @Nullable final DatabaseCallback<Void> callback) {

        writeData(USERS_PATH + "/" + user.getId(), user, callback);
        // מעדכן את פרטי המשתמש.
    }

    public void getUserList(@NotNull final DatabaseCallback<List<User>> callback) {
        getDataList(USERS_PATH, User.class, callback);
        // מביא את כל המשתמשים מהמסד.
    }

    public void deleteUser(String uid,
                           DatabaseCallback<Void> callback) {

        readData(USERS_PATH + "/" + uid).removeValue().addOnCompleteListener(task -> {
            // מוחק משתמש לפי ה-uid שלו.

            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
                // אם המחיקה הצליחה.
            } else {
                if (callback != null) callback.onFailed(task.getException());
                // אם המחיקה נכשלה.
            }
        });
    }

    public void loginUser(@NotNull final String email,
                          @NotNull final String password,
                          @Nullable final DatabaseCallback<String> callback) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // מקבל את Firebase Authentication.

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            // מנסה להתחבר עם אימייל וסיסמה.

            if (task.isSuccessful()) {
                String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                        ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                        : "";
                // אם ההתחברות הצליחה, לוקחים את מזהה המשתמש.

                if (callback != null) callback.onCompleted(uid);
                // מחזיר את ה-uid למסך ההתחברות.
            } else {
                if (callback != null) callback.onFailed(task.getException());
                // אם ההתחברות נכשלה, מחזיר שגיאה.
            }
        });
    }

    public void createNewCourt(@NotNull final Court court,
                               @Nullable final DatabaseCallback<Void> callback) {

        writeData(COURT_PATH + "/" + court.getId(), court, callback);
        // שומר מגרש חדש ב-Firebase.
    }

    public void getCourt(@NotNull final String courtId,
                         @NotNull final DatabaseCallback<Court> callback) {

        getData(COURT_PATH + "/" + courtId, Court.class, callback);
        // מביא מגרש לפי ה-id שלו.
    }

    public void getCourtsList(@NotNull final DatabaseCallback<List<Court>> callback) {
        getDataList(COURT_PATH, Court.class, callback);
        // מביא את כל המגרשים.
    }

    public void getUsers(@NotNull final DatabaseCallback<List<User>> callback) {
        getDataList(USERS_PATH, User.class, callback);
        // מביא את כל המשתמשים.
    }

    public void updateCourt(@NonNull Court court,
                            @Nullable DatabaseCallback<Void> callback) {

        writeData(COURT_PATH + "/" + court.getId(), court, callback);
        // מעדכן מגרש קיים.
    }

    public void deleteCourt(@NonNull String courtId,
                            @Nullable DatabaseCallback<Void> callback) {

        readData(COURT_PATH + "/" + courtId).removeValue((error, ref) -> {
            // מוחק מגרש לפי ה-id שלו.

            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
                // אם הייתה שגיאה במחיקה.
            } else {
                if (callback != null) callback.onCompleted(null);
                // אם המחיקה הצליחה.
            }
        });
    }

    public void setUserAdmin(@NonNull String userId,
                             boolean isAdmin,
                             @Nullable DatabaseCallback<Void> callback) {

        readData(USERS_PATH + "/" + userId + "/isAdmin").setValue(isAdmin, (error, ref) -> {
            // משנה רק את השדה isAdmin של המשתמש.

            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
                // אם העדכון נכשל.
            } else {
                if (callback != null) callback.onCompleted(null);
                // אם העדכון הצליח.
            }
        });
    }

    public static String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        // מחזיר את התאריך של היום בפורמט קבוע.
    }

    public static String getDatePlusDays(int days) {
        long time = System.currentTimeMillis() + (long) days * 24 * 60 * 60 * 1000;
        // מחשב את הזמן לפי מספר ימים קדימה.

        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(time));
        // מחזיר את התאריך העתידי בפורמט קבוע.
    }

    public void getReservationsForCourtOnDate(@NonNull String courtId,
                                              @NonNull String date,
                                              @NonNull DatabaseCallback<Map<String, Reservation>> callback) {

        readData(RESERVATIONS_PATH + "/" + date + "/" + courtId).get().addOnCompleteListener(task -> {
            // מביא את כל ההזמנות של מגרש מסוים בתאריך מסוים.

            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
                // אם הקריאה נכשלה, מחזירים שגיאה.
            }

            Map<String, Reservation> result = new HashMap<>();
            // שומר את ההזמנות במפה לפי slotId.

            for (DataSnapshot snap : task.getResult().getChildren()) {
                Reservation reservation = snap.getValue(Reservation.class);
                // ממיר כל נתון להזמנה.

                if (reservation != null) {
                    result.put(snap.getKey(), reservation);
                    // מוסיף את ההזמנה למפה.
                }
            }

            callback.onCompleted(result);
            // מחזיר את ההזמנות למסך.
        });
    }

    public void getReservationsForUser(@NonNull String userId,
                                       @NonNull DatabaseCallback<List<Reservation>> callback) {

        readData(RESERVATIONS_PATH).get().addOnCompleteListener(task -> {
            // קורא את כל ההזמנות כדי למצוא את ההזמנות של המשתמש.

            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
                // אם הקריאה נכשלה, מחזירים שגיאה.
            }

            List<Reservation> reservationList = new ArrayList<>();
            // רשימה שתכיל רק את ההזמנות של המשתמש.

            for (DataSnapshot dateSnap : task.getResult().getChildren()) {
                // עובר על כל התאריכים.

                for (DataSnapshot courtSnap : dateSnap.getChildren()) {
                    // עובר על כל המגרשים באותו תאריך.

                    for (DataSnapshot slotSnap : courtSnap.getChildren()) {
                        // עובר על כל השעות באותו מגרש.

                        Reservation reservation = slotSnap.getValue(Reservation.class);
                        // ממיר את הנתון להזמנה.

                        if (reservation != null && userId.equals(reservation.getUserId())) {
                            reservationList.add(reservation);
                            // מוסיף רק הזמנות ששייכות למשתמש הזה.
                        }
                    }
                }
            }

            callback.onCompleted(reservationList);
            // מחזיר את כל ההזמנות של המשתמש.
        });
    }

    public void cancelReservation(@NonNull Reservation reservation,
                                  @NonNull DatabaseCallback<Void> callback) {

        String path = RESERVATIONS_PATH + "/"
                + reservation.getBookingDate() + "/"
                + reservation.getCourtId() + "/"
                + reservation.getSlotId();
        // בונה את הנתיב המדויק של ההזמנה.

        readData(path).removeValue((error, ref) -> {
            // מוחק את ההזמנה מהמסד.

            if (error != null) {
                callback.onFailed(error.toException());
                // אם המחיקה נכשלה.
            } else {
                callback.onCompleted(null);
                // אם המחיקה הצליחה.
            }
        });
    }

    private boolean isOverlapping(String start1,
                                  String end1,
                                  String start2,
                                  String end2) {

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
            // אם אחת השעות חסרה, לא מחשיבים כחפיפה.
        }

        return start1.compareTo(end2) < 0 && start2.compareTo(end1) < 0;
        // בודק אם שני טווחי שעות חופפים.
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
        // הפניה לכל ההזמנות של אותו תאריך.

        AtomicReference<String> failReason = new AtomicReference<>("");
        // שומר את סיבת הכישלון אם ההזמנה נכשלת.

        dayRef.runTransaction(new Transaction.Handler() {
            // Transaction מונע מצב ששני משתמשים יזמינו את אותה שעה ביחד.

            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                int userReservationCount = 0;
                // סופר כמה הזמנות יש למשתמש באותו יום.

                for (MutableData courtNode : currentData.getChildren()) {
                    // עובר על כל המגרשים באותו תאריך.

                    for (MutableData slotNode : courtNode.getChildren()) {
                        // עובר על כל השעות בכל מגרש.

                        Object value = slotNode.getValue();
                        // לוקח את הערך של ההזמנה.

                        if (!(value instanceof Map)) continue;
                        // אם זה לא מבנה של הזמנה, מדלגים.

                        Map<?, ?> map = (Map<?, ?>) value;
                        // הופך את הערך למפה כדי לקרוא שדות.

                        String savedUserId = map.get("userId") != null ? map.get("userId").toString() : null;
                        // לוקח את מזהה המשתמש מההזמנה הקיימת.

                        String savedStart = map.get("startTime") != null ? map.get("startTime").toString() : null;
                        // לוקח את שעת ההתחלה של הזמנה קיימת.

                        String savedEnd = map.get("endTime") != null ? map.get("endTime").toString() : null;
                        // לוקח את שעת הסיום של הזמנה קיימת.

                        if (savedUserId == null) continue;
                        // אם אין מזהה משתמש, מדלגים.

                        if (userId.equals(savedUserId)) {
                            userReservationCount++;
                            // אם ההזמנה שייכת למשתמש, מעלים את הספירה.

                            if (isOverlapping(savedStart, savedEnd, startTime, endTime)) {
                                failReason.set("You already have a reservation at this time.");
                                // שומר סיבת כישלון.

                                return Transaction.abort();
                                // מבטל את ההזמנה.
                            }
                        }
                    }
                }

                if (userReservationCount >= 2) {
                    failReason.set("You can reserve up to 2 time slots only.");
                    // שומר סיבת כישלון.

                    return Transaction.abort();
                    // מבטל אם יש למשתמש כבר 2 הזמנות ביום.
                }

                MutableData courtNode = currentData.child(court.getId());
                // מגיע למגרש שנבחר.

                MutableData slotNode = courtNode.child(slotId);
                // מגיע לשעה שנבחרה.

                if (slotNode.getValue() != null) {
                    failReason.set("This slot is already reserved.");
                    // שומר שהשעה כבר תפוסה.

                    return Transaction.abort();
                    // מבטל את ההזמנה.
                }

                String reservationId = readData(RESERVATIONS_PATH).push().getKey();
                // יוצר מזהה חדש להזמנה.

                Map<String, Object> reservationMap = new HashMap<>();
                // יוצר מפה שתישמר כהזמנה ב-Firebase.

                reservationMap.put("id", reservationId);
                reservationMap.put("courtId", court.getId());
                reservationMap.put("userId", userId);
                reservationMap.put("userName", userName);
                reservationMap.put("slotId", slotId);
                reservationMap.put("startTime", startTime);
                reservationMap.put("endTime", endTime);
                reservationMap.put("bookingDate", bookingDate);
                reservationMap.put("createdAt", System.currentTimeMillis());
                // מכניס את כל פרטי ההזמנה.

                slotNode.setValue(reservationMap);
                // שומר את ההזמנה בתוך השעה המתאימה.

                return Transaction.success(currentData);
                // מאשר את ההזמנה.
            }

            @Override
            public void onComplete(@Nullable DatabaseError error,
                                   boolean committed,
                                   @Nullable DataSnapshot currentData) {

                if (error != null) {
                    callback.onFailed(error.toException());
                    return;
                    // אם הייתה שגיאת Firebase, מחזירים שגיאה.
                }

                if (!committed) {
                    callback.onFailed(new Exception(
                            failReason.get() == null || failReason.get().isEmpty()
                                    ? "Reservation failed."
                                    : failReason.get()
                    ));
                    return;
                    // אם העסקה בוטלה, מחזירים את הסיבה.
                }

                callback.onCompleted("Reservation successful!");
                // אם הכל הצליח, מחזירים הודעת הצלחה.
            }
        });
    }
}