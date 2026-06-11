package com.alma.ilaymidler_finalproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.Model.Reservation;
import com.alma.ilaymidler_finalproject.Model.ReservationDisplayItem;
import com.alma.ilaymidler_finalproject.adapters.MyReservationsAdapter;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MyReservationsActivity extends BaseMenuActivity {

    private RecyclerView rvMyReservations;
    // הרשימה שמציגה את כל ההזמנות של המשתמש.

    private TextView tvEmpty;
    // טקסט שמוצג כאשר אין הזמנות או כשיש שגיאה.

    private ProgressBar progressBar;
    // סימן טעינה בזמן שמביאים או מוחקים הזמנות.

    private DatabaseService databaseService;
    // השירות שאחראי על פעולות מול Firebase.

    private MyReservationsAdapter adapter;
    // Adapter שמציג את ההזמנות במסך.

    private boolean loadingInProgress = false;
    // מונע טעינה כפולה של ההזמנות באותו זמן.

    private boolean cancelInProgress = false;
    // מונע ביטול כפול של אותה הזמנה.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מפעיל את onCreate של המחלקה האב.

        setContentView(R.layout.activity_my_reservations);
        // טוען את העיצוב של מסך ההזמנות שלי.

        setupToolbar(R.id.topToolbar, "My Reservations");
        // מגדיר Toolbar עם הכותרת My Reservations.

        rvMyReservations = findViewById(R.id.rvMyReservations);
        // מחבר את RecyclerView מה-XML לקוד.

        tvEmpty = findViewById(R.id.tvEmpty);
        // מחבר את הודעת הריק מה-XML לקוד.

        progressBar = findViewById(R.id.progressBar);
        // מחבר את סימן הטעינה מה-XML לקוד.

        databaseService = DatabaseService.getInstance();
        // מקבל את DatabaseService כדי לעבוד מול Firebase.

        rvMyReservations.setLayoutManager(new LinearLayoutManager(this));
        // מגדיר שההזמנות יוצגו כרשימה אנכית.

        adapter = new MyReservationsAdapter(this::showCancelDialog);
        // יוצר Adapter.
        // כשמשתמש לוחץ ביטול, תיפתח פונקציית showCancelDialog.

        rvMyReservations.setAdapter(adapter);
        // מחבר את ה-Adapter לרשימה.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // מופעל בכל פעם שחוזרים למסך.

        loadMyReservations();
        // טוען מחדש את ההזמנות של המשתמש.
    }

    private void loadMyReservations() {
        // הפונקציה טוענת את כל ההזמנות של המשתמש המחובר.

        if (loadingInProgress) {
            return;
            // אם כבר מתבצעת טעינה, לא מתחילים עוד אחת.
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // מקבל את המשתמש שמחובר כרגע.

        if (firebaseUser == null) {
            Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            // אם אין משתמש מחובר, מציג הודעה.

            finish();
            // סוגר את המסך.

            return;
            // עוצר את הפונקציה.
        }

        loadingInProgress = true;
        // מסמן שהתחילה טעינה.

        String userId = firebaseUser.getUid();
        // שומר את מזהה המשתמש המחובר.

        progressBar.setVisibility(View.VISIBLE);
        // מציג טעינה.

        rvMyReservations.setVisibility(View.GONE);
        // מסתיר את הרשימה בזמן טעינה.

        tvEmpty.setVisibility(View.GONE);
        // מסתיר את הודעת הריק בזמן טעינה.

        databaseService.getReservationsForUser(userId, new DatabaseService.DatabaseCallback<List<Reservation>>() {
            // מבקש מ-Firebase את כל ההזמנות של המשתמש.

            @Override
            public void onCompleted(List<Reservation> reservations) {
                loadingInProgress = false;
                // מסמן שהטעינה הסתיימה.

                if (reservations == null || reservations.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    // מסתיר טעינה.

                    rvMyReservations.setVisibility(View.GONE);
                    // מסתיר את הרשימה.

                    tvEmpty.setVisibility(View.VISIBLE);
                    // מציג הודעת ריק.

                    tvEmpty.setText("You have no reservations yet");
                    // מציג שאין הזמנות.

                    adapter.updateList(new ArrayList<>());
                    // מנקה את הרשימה במסך.

                    return;
                    // עוצר את הפונקציה.
                }

                loadCourtDetailsForReservations(reservations);
                // אם יש הזמנות, טוען גם את פרטי המגרשים שלהן.
            }

            @Override
            public void onFailed(Exception e) {
                loadingInProgress = false;
                // מסמן שהטעינה הסתיימה.

                progressBar.setVisibility(View.GONE);
                // מסתיר טעינה.

                rvMyReservations.setVisibility(View.GONE);
                // מסתיר את הרשימה.

                tvEmpty.setVisibility(View.VISIBLE);
                // מציג הודעת שגיאה.

                tvEmpty.setText("Failed to load reservations");
                // שם טקסט שגיאה.

                Toast.makeText(MyReservationsActivity.this, "Failed to load reservations", Toast.LENGTH_SHORT).show();
                // מציג Toast שגיאה.
            }
        });
    }

    private void loadCourtDetailsForReservations(List<Reservation> reservations) {
        // הפונקציה טוענת את פרטי המגרש עבור כל הזמנה.

        List<ReservationDisplayItem> displayItems = new ArrayList<>();
        // רשימה שתכיל הזמנה + המגרש שלה.

        if (reservations == null || reservations.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            // מסתיר טעינה.

            rvMyReservations.setVisibility(View.GONE);
            // מסתיר את הרשימה.

            tvEmpty.setVisibility(View.VISIBLE);
            // מציג הודעת ריק.

            tvEmpty.setText("You have no reservations yet");
            // מציג שאין הזמנות.

            adapter.updateList(new ArrayList<>());
            // מנקה את הרשימה.

            return;
            // עוצר את הפונקציה.
        }

        final int total = reservations.size();
        // סך כל ההזמנות שצריך לטעון להן מגרשים.

        final int[] loadedCount = {0};
        // סופר כמה מגרשים כבר נטענו.
        // משתמשים במערך כי אנחנו בתוך callback.

        for (Reservation reservation : reservations) {
            // עובר על כל הזמנה.

            if (reservation == null) {
                loadedCount[0]++;
                // אם ההזמנה לא תקינה, עדיין מעלים את הספירה.

                checkIfAllCourtDetailsLoaded(displayItems, loadedCount[0], total);
                // בודקים אם סיימנו.

                continue;
                // עוברים להזמנה הבאה.
            }

            databaseService.getCourt(reservation.getCourtId(), new DatabaseService.DatabaseCallback<Court>() {
                // מביא את פרטי המגרש של ההזמנה.

                @Override
                public void onCompleted(Court court) {
                    displayItems.add(new ReservationDisplayItem(reservation, court));
                    // מוסיף לרשימה אובייקט שמכיל הזמנה + מגרש.

                    loadedCount[0]++;
                    // מעלה את מספר הפריטים שנטענו.

                    checkIfAllCourtDetailsLoaded(displayItems, loadedCount[0], total);
                    // בודק אם כל המגרשים כבר נטענו.
                }

                @Override
                public void onFailed(Exception e) {
                    displayItems.add(new ReservationDisplayItem(reservation, null));
                    // אם טעינת המגרש נכשלה, עדיין מציגים את ההזמנה בלי מגרש.

                    loadedCount[0]++;
                    // מעלה את מספר הפריטים שנטענו.

                    checkIfAllCourtDetailsLoaded(displayItems, loadedCount[0], total);
                    // בודק אם סיימנו לטעון הכל.
                }
            });
        }
    }

    private void checkIfAllCourtDetailsLoaded(List<ReservationDisplayItem> items,
                                              int loadedCount,
                                              int totalItems) {
        // הפונקציה בודקת אם כל פרטי המגרשים כבר נטענו.

        if (loadedCount != totalItems) {
            return;
            // אם עדיין לא הכל נטען, לא מעדכנים את המסך.
        }

        progressBar.setVisibility(View.GONE);
        // מסתיר טעינה.

        if (items == null || items.isEmpty()) {
            rvMyReservations.setVisibility(View.GONE);
            // מסתיר את הרשימה.

            tvEmpty.setVisibility(View.VISIBLE);
            // מציג הודעת ריק.

            tvEmpty.setText("You have no reservations yet");
            // מציג שאין הזמנות.

            adapter.updateList(new ArrayList<>());
            // מנקה את הרשימה.

            return;
            // עוצר את הפונקציה.
        }

        adapter.updateList(items);
        // מעדכן את הרשימה עם ההזמנות והמגרשים.

        rvMyReservations.setVisibility(View.VISIBLE);
        // מציג את הרשימה.

        tvEmpty.setVisibility(View.GONE);
        // מסתיר את הודעת הריק.
    }

    private void showCancelDialog(ReservationDisplayItem item) {
        // הפונקציה מציגה חלון אישור לפני ביטול הזמנה.

        if (item == null || item.getReservation() == null || cancelInProgress) {
            return;
            // אם אין הזמנה תקינה או שכבר יש ביטול פעיל, לא עושים כלום.
        }

        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                // כותרת חלון הביטול.

                .setMessage("Are you sure you want to cancel this reservation?")
                // שאלה למשתמש אם הוא בטוח.

                .setPositiveButton("Yes", (dialog, which) -> cancelReservation(item.getReservation()))
                // אם לוחצים Yes, מבטלים את ההזמנה.

                .setNegativeButton("No", null)
                // אם לוחצים No, החלון נסגר.

                .show();
        // מציג את חלון האישור.
    }

    private void cancelReservation(Reservation reservation) {
        // הפונקציה מבטלת הזמנה קיימת.

        if (reservation == null || cancelInProgress) {
            return;
            // אם אין הזמנה או שכבר יש ביטול פעיל, עוצרים.
        }

        cancelInProgress = true;
        // מסמן שהתחיל ביטול.

        progressBar.setVisibility(View.VISIBLE);
        // מציג טעינה.

        databaseService.cancelReservation(reservation, new DatabaseService.DatabaseCallback<Void>() {
            // שולח בקשה ל-Firebase למחוק את ההזמנה.

            @Override
            public void onCompleted(Void object) {
                cancelInProgress = false;
                // מסמן שהביטול הסתיים.

                Toast.makeText(MyReservationsActivity.this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                // מציג הודעת הצלחה.

                loadMyReservations();
                // טוען מחדש את ההזמנות אחרי הביטול.
            }

            @Override
            public void onFailed(Exception e) {
                cancelInProgress = false;
                // מסמן שהביטול הסתיים גם אם נכשל.

                progressBar.setVisibility(View.GONE);
                // מסתיר טעינה.

                Toast.makeText(MyReservationsActivity.this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
                // מציג הודעת שגיאה.
            }
        });
    }
}