package com.alma.ilaymidler_finalproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.Model.Reservation;
import com.alma.ilaymidler_finalproject.Model.TimeSlot;
import com.alma.ilaymidler_finalproject.adapters.TimeSlotAdapter;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.alma.ilaymidler_finalproject.utils.NotificationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CourtDetailsActivity extends BaseMenuActivity {

    private TextView tvCourtName, tvCourtAddress, tvCourtType, tvDate;
    // טקסטים שמציגים את שם המגרש, כתובת, סוג ותאריך נבחר.

    private RecyclerView rvTimeSlots;
    // רשימה שמציגה את כל שעות ההזמנה.

    private ProgressBar progressBar;
    // סימן טעינה בזמן שמביאים נתונים או מבצעים הזמנה.

    private Button btnChooseDate;
    // כפתור לבחירת תאריך.

    private DatabaseService databaseService;
    // השירות שאחראי על כל הפעולות מול Firebase.

    private Court court;
    // המגרש שהמשתמש פתח את הפרטים שלו.

    private TimeSlotAdapter adapter;
    // Adapter שמציג את שעות ההזמנה.

    private final List<TimeSlot> allSlots = new ArrayList<>();
    // רשימה שמחזיקה את כל שעות ההזמנה של אותו יום.

    private Calendar selectedCalendar;
    // שומר את התאריך שהמשתמש בחר.

    private boolean bookingInProgress = false;
    // מונע מהמשתמש ללחוץ כמה פעמים על הזמנה לפני ש-Firebase מסיים.

    private final androidx.activity.result.ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // מקבל תשובה מהמשתמש האם הוא אישר התראות או לא.
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מפעיל את onCreate של המחלקה האב.

        setContentView(R.layout.activity_court_details);
        // טוען את העיצוב של מסך פרטי מגרש.

        setupToolbar(R.id.topToolbar, "Court Details");
        // מגדיר Toolbar עם הכותרת Court Details.

        tvCourtName = findViewById(R.id.tvCourtName);
        // מחבר את שם המגרש מה-XML לקוד.

        tvCourtAddress = findViewById(R.id.tvCourtAddress);
        // מחבר את כתובת המגרש מה-XML לקוד.

        tvCourtType = findViewById(R.id.tvCourtType);
        // מחבר את סוג המגרש מה-XML לקוד.

        tvDate = findViewById(R.id.tvDate);
        // מחבר את טקסט התאריך מה-XML לקוד.

        rvTimeSlots = findViewById(R.id.rvTimeSlots);
        // מחבר את רשימת השעות מה-XML לקוד.

        progressBar = findViewById(R.id.progressBar);
        // מחבר את סימן הטעינה מה-XML לקוד.

        btnChooseDate = findViewById(R.id.btnChooseDate);
        // מחבר את כפתור בחירת התאריך מה-XML לקוד.

        databaseService = DatabaseService.getInstance();
        // מקבל את DatabaseService כדי לעבוד מול Firebase.

        NotificationHelper.createNotificationChannel(this);
        // יוצר ערוץ התראות לאפליקציה.

        requestNotificationPermissionIfNeeded();
        // מבקש הרשאת התראות אם צריך.

        selectedCalendar = Calendar.getInstance();
        // ברירת מחדל: התאריך שנבחר הוא היום.

        rvTimeSlots.setLayoutManager(new LinearLayoutManager(this));
        // מגדיר שהשעות יוצגו כרשימה אנכית.

        adapter = new TimeSlotAdapter(new ArrayList<>(), this::showReserveConfirmationDialog);
        // יוצר Adapter לשעות.
        // כאשר לוחצים Reserve, נפתח דיאלוג אישור.

        rvTimeSlots.setAdapter(adapter);
        // מחבר את ה-Adapter ל-RecyclerView.

        btnChooseDate.setOnClickListener(v -> openDatePicker());
        // כאשר לוחצים על הכפתור, נפתח חלון לבחירת תאריך.

        String courtId = getIntent().getStringExtra("COURT_ID");
        // מקבל את מזהה המגרש שהועבר מהמסך הקודם.

        if (courtId == null || courtId.trim().isEmpty()) {
            Toast.makeText(this, "Court not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
            // אם אין מזהה מגרש, סוגרים את המסך.
        }

        updateDateLabel();
        // מציג את התאריך הנבחר במסך.

        loadCourt(courtId);
        // טוען את פרטי המגרש מ-Firebase.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // מופעל בכל פעם שחוזרים למסך הזה.

        if (court != null) {
            loadReservationsForSelectedDate();
            // אם המגרש כבר נטען, מרעננים את השעות.
        }
    }

    private void openDatePicker() {
        // הפונקציה פותחת חלון בחירת תאריך.

        Calendar today = Calendar.getInstance();
        // שומר את התאריך של היום.

        Calendar maxDay = Calendar.getInstance();
        // שומר את התאריך המקסימלי לבחירה.

        maxDay.add(Calendar.DAY_OF_YEAR, 7);
        // מאפשר לבחור עד 7 ימים קדימה.

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    Calendar picked = Calendar.getInstance();
                    // יוצר אובייקט תאריך חדש לפי הבחירה.

                    picked.set(Calendar.YEAR, year);
                    picked.set(Calendar.MONTH, month);
                    picked.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    picked.set(Calendar.HOUR_OF_DAY, 0);
                    picked.set(Calendar.MINUTE, 0);
                    picked.set(Calendar.SECOND, 0);
                    picked.set(Calendar.MILLISECOND, 0);
                    // מאפס את השעה כדי להשוות רק לפי תאריך.

                    Calendar compareToday = Calendar.getInstance();
                    compareToday.set(Calendar.HOUR_OF_DAY, 0);
                    compareToday.set(Calendar.MINUTE, 0);
                    compareToday.set(Calendar.SECOND, 0);
                    compareToday.set(Calendar.MILLISECOND, 0);
                    // יוצר תאריך של היום בלי שעות.

                    Calendar compareMax = Calendar.getInstance();
                    compareMax.set(Calendar.HOUR_OF_DAY, 0);
                    compareMax.set(Calendar.MINUTE, 0);
                    compareMax.set(Calendar.SECOND, 0);
                    compareMax.set(Calendar.MILLISECOND, 0);
                    compareMax.add(Calendar.DAY_OF_YEAR, 7);
                    // יוצר תאריך מקסימלי של 7 ימים קדימה.

                    if (picked.before(compareToday)) {
                        Toast.makeText(this, "You can't reserve past dates", Toast.LENGTH_SHORT).show();
                        return;
                        // לא מאפשר להזמין תאריך שעבר.
                    }

                    if (picked.after(compareMax)) {
                        Toast.makeText(this, "You can only book within the next 7 days", Toast.LENGTH_SHORT).show();
                        return;
                        // לא מאפשר להזמין יותר מ-7 ימים קדימה.
                    }

                    selectedCalendar = picked;
                    // שומר את התאריך שנבחר.

                    updateDateLabel();
                    // מעדכן את התאריך שמוצג במסך.

                    loadReservationsForSelectedDate();
                    // טוען מחדש את שעות ההזמנה לפי התאריך החדש.
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMinDate(today.getTimeInMillis());
        // מגביל את הבחירה מהיום והלאה.

        dialog.getDatePicker().setMaxDate(maxDay.getTimeInMillis());
        // מגביל את הבחירה עד 7 ימים קדימה.

        dialog.show();
        // מציג את חלון בחירת התאריך.
    }

    private String getSelectedDateString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCalendar.getTime());
        // מחזיר את התאריך בפורמט שמתאים לשמירה ב-Firebase.
    }

    private String getSelectedDateDisplayString() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCalendar.getTime());
        // מחזיר את התאריך בפורמט נוח להצגה למשתמש.
    }

    private void updateDateLabel() {
        tvDate.setText("Selected date: " + getSelectedDateDisplayString());
        // מציג במסך את התאריך שנבחר.
    }

    private void loadCourt(String courtId) {
        // הפונקציה טוענת את פרטי המגרש לפי id.

        progressBar.setVisibility(View.VISIBLE);
        // מציג טעינה.

        databaseService.getCourt(courtId, new DatabaseService.DatabaseCallback<Court>() {
            @Override
            public void onCompleted(Court object) {
                court = object;
                // שומר את המגרש שהתקבל.

                if (court == null) {
                    Toast.makeText(CourtDetailsActivity.this, "Court not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                    // אם המגרש לא נמצא, סוגרים את המסך.
                }

                tvCourtName.setText(court.getName());
                // מציג את שם המגרש.

                tvCourtAddress.setText(court.getAddress());
                // מציג את כתובת המגרש.

                tvCourtType.setText(court.getType());
                // מציג את סוג המגרש.

                allSlots.clear();
                // מנקה את רשימת השעות הישנה.

                allSlots.addAll(TimeSlot.generateDailySlots());
                // יוצר את כל שעות היום מחדש.

                loadReservationsForSelectedDate();
                // טוען את ההזמנות של התאריך הנבחר.
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                // מסתיר טעינה.

                Toast.makeText(CourtDetailsActivity.this, "Failed loading court", Toast.LENGTH_SHORT).show();
                // מציג הודעת שגיאה.
            }
        });
    }

    private void loadReservationsForSelectedDate() {
        // הפונקציה טוענת את כל ההזמנות של המגרש בתאריך שנבחר.

        if (court == null) {
            return;
            // אם אין מגרש, אין מה לטעון.
        }

        progressBar.setVisibility(View.VISIBLE);
        // מציג טעינה.

        databaseService.getReservationsForCourtOnDate(
                court.getId(),
                getSelectedDateString(),
                new DatabaseService.DatabaseCallback<Map<String, Reservation>>() {
                    @Override
                    public void onCompleted(Map<String, Reservation> reservations) {

                        for (TimeSlot slot : allSlots) {
                            // עובר על כל שעות היום.

                            slot.setReserved(false);
                            // קודם מסמן כל שעה כפנויה.

                            slot.setReservedByUserId("");
                            // מוחק את המשתמש שהזמין, אם היה.

                            if (reservations != null && reservations.containsKey(slot.getId())) {
                                // אם יש הזמנה לשעה הזאת.

                                Reservation reservation = reservations.get(slot.getId());
                                // מקבל את ההזמנה של אותה שעה.

                                slot.setReserved(true);
                                // מסמן את השעה כתפוסה.

                                if (reservation != null) {
                                    slot.setReservedByUserId(reservation.getUserId());
                                    // שומר מי הזמין את השעה.
                                }
                            }
                        }

                        adapter.updateList(allSlots);
                        // מעדכן את רשימת השעות במסך.

                        progressBar.setVisibility(View.GONE);
                        // מסתיר טעינה.
                    }

                    @Override
                    public void onFailed(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        // מסתיר טעינה.

                        Toast.makeText(CourtDetailsActivity.this, "Failed loading slots", Toast.LENGTH_SHORT).show();
                        // מציג הודעת שגיאה.
                    }
                }
        );
    }

    private void showReserveConfirmationDialog(TimeSlot slot) {
        // הפונקציה פותחת חלון אישור לפני ביצוע הזמנה.

        if (slot == null || court == null) {
            return;
            // אם אין שעה או מגרש, לא ממשיכים.
        }

        if (slot.isReserved()) {
            Toast.makeText(this, "This slot is unavailable", Toast.LENGTH_SHORT).show();
            return;
            // אם השעה כבר תפוסה, לא מאפשרים להזמין.
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            return;
            // אם המשתמש לא מחובר, לא מאפשרים להזמין.
        }

        String message =
                "Court: " + court.getName() + "\n\n" +
                        "Date: " + getSelectedDateDisplayString() + "\n" +
                        "Time: " + slot.getStartTime() + " - " + slot.getEndTime() + "\n\n" +
                        "Do you want to confirm this reservation?";
        // יוצר טקסט שמציג למשתמש את פרטי ההזמנה לפני אישור.

        new AlertDialog.Builder(this)
                .setTitle("Confirm Reservation")
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> reserveSlot(slot))
                .setNegativeButton("Cancel", null)
                .show();
        // מציג דיאלוג אישור.
    }

    private void reserveSlot(TimeSlot slot) {
        // הפונקציה מבצעת את ההזמנה בפועל.

        if (bookingInProgress) {
            return;
            // אם כבר מתבצעת הזמנה, לא מאפשרים עוד לחיצה.
        }

        bookingInProgress = true;
        // מסמנים שהתחילה הזמנה.

        if (slot.isReserved()) {
            bookingInProgress = false;
            Toast.makeText(this, "This slot is unavailable", Toast.LENGTH_SHORT).show();
            return;
            // אם השעה תפוסה, עוצרים.
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // מקבל את המשתמש המחובר.

        if (firebaseUser == null) {
            bookingInProgress = false;
            Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            return;
            // אם אין משתמש מחובר, עוצרים.
        }

        String selectedDate = getSelectedDateString();
        // שומר את התאריך שנבחר בפורמט Firebase.

        String today = DatabaseService.getTodayDate();
        // שומר את התאריך של היום.

        String maxDate = DatabaseService.getDatePlusDays(7);
        // שומר את התאריך המקסימלי להזמנה.

        if (selectedDate.compareTo(today) < 0) {
            bookingInProgress = false;
            Toast.makeText(this, "You can't reserve past dates", Toast.LENGTH_SHORT).show();
            return;
            // לא מאפשר להזמין תאריך שעבר.
        }

        if (selectedDate.compareTo(maxDate) > 0) {
            bookingInProgress = false;
            Toast.makeText(this, "You can only book within the next 7 days", Toast.LENGTH_SHORT).show();
            return;
            // לא מאפשר להזמין יותר מ-7 ימים קדימה.
        }

        progressBar.setVisibility(View.VISIBLE);
        // מציג טעינה בזמן ההזמנה.

        String userId = firebaseUser.getUid();
        // מזהה המשתמש המחובר.

        String email = firebaseUser.getEmail();
        // האימייל של המשתמש.

        String userName = email != null ? email : "User";
        // שם שיופיע בהזמנה.
        // כרגע משתמשים באימייל אם קיים.

        databaseService.reserveCourtSlot(
                court,
                selectedDate,
                userId,
                userName,
                slot.getId(),
                slot.getStartTime(),
                slot.getEndTime(),
                new DatabaseService.DatabaseCallback<String>() {
                    @Override
                    public void onCompleted(String object) {
                        bookingInProgress = false;
                        // מסמנים שההזמנה הסתיימה.

                        progressBar.setVisibility(View.GONE);
                        // מסתירים טעינה.

                        Toast.makeText(CourtDetailsActivity.this, object, Toast.LENGTH_SHORT).show();
                        // מציגים הודעת הצלחה.

                        NotificationHelper.showReservationNotification(
                                CourtDetailsActivity.this,
                                court.getName(),
                                selectedDate,
                                slot.getStartTime(),
                                slot.getEndTime()
                        );
                        // מציגים התראה שההזמנה אושרה.

                        NotificationHelper.scheduleReservationReminder(
                                CourtDetailsActivity.this,
                                court.getName(),
                                selectedDate,
                                slot.getStartTime(),
                                slot.getEndTime()
                        );
                        // קובעים תזכורת לפני ההזמנה.

                        loadReservationsForSelectedDate();
                        // מרעננים את השעות אחרי ההזמנה.
                    }

                    @Override
                    public void onFailed(Exception e) {
                        bookingInProgress = false;
                        // מסמנים שההזמנה הסתיימה גם אם נכשלה.

                        progressBar.setVisibility(View.GONE);
                        // מסתירים טעינה.

                        Toast.makeText(CourtDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        // מציגים את סיבת הכישלון.

                        loadReservationsForSelectedDate();
                        // מרעננים את הרשימה כדי לקבל מצב עדכני.
                    }
                }
        );
    }

    private void requestNotificationPermissionIfNeeded() {
        // הפונקציה מבקשת הרשאת התראות אם צריך.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // רק Android 13 ומעלה דורש הרשאת התראות בזמן ריצה.

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // בודק אם עדיין אין הרשאת התראות.

                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                // מבקש מהמשתמש לאשר התראות.
            }
        }
    }
}