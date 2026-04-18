package com.alma.ilaymidler_finalproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CourtDetailsActivity extends BaseMenuActivity {

    private TextView tvCourtName, tvCourtAddress, tvCourtType, tvDate;
    private RecyclerView rvTimeSlots;
    private ProgressBar progressBar;
    private Button btnChooseDate;

    private DatabaseService databaseService;
    private Court court;
    private TimeSlotAdapter adapter;

    private final List<TimeSlot> allSlots = new ArrayList<>();
    private Calendar selectedCalendar;

    private final androidx.activity.result.ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_details);

        setupToolbar(R.id.topToolbar, "Court Details");

        tvCourtName = findViewById(R.id.tvCourtName);
        tvCourtAddress = findViewById(R.id.tvCourtAddress);
        tvCourtType = findViewById(R.id.tvCourtType);
        tvDate = findViewById(R.id.tvDate);
        rvTimeSlots = findViewById(R.id.rvTimeSlots);
        progressBar = findViewById(R.id.progressBar);
        btnChooseDate = findViewById(R.id.btnChooseDate);

        databaseService = DatabaseService.getInstance();
        NotificationHelper.createNotificationChannel(this);
        requestNotificationPermissionIfNeeded();

        selectedCalendar = Calendar.getInstance();

        rvTimeSlots.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TimeSlotAdapter(new ArrayList<>(), this::showReserveConfirmationDialog);
        rvTimeSlots.setAdapter(adapter);

        btnChooseDate.setOnClickListener(v -> openDatePicker());

        String courtId = getIntent().getStringExtra("COURT_ID");
        if (courtId == null || courtId.trim().isEmpty()) {
            Toast.makeText(this, "Court not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateDateLabel();
        loadCourt(courtId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (court != null) {
            loadReservationsForSelectedDate();
        }
    }

    private void openDatePicker() {
        Calendar today = Calendar.getInstance();
        Calendar maxDay = Calendar.getInstance();
        maxDay.add(Calendar.DAY_OF_YEAR, 7);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar picked = Calendar.getInstance();
                    picked.set(Calendar.YEAR, year);
                    picked.set(Calendar.MONTH, month);
                    picked.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    picked.set(Calendar.HOUR_OF_DAY, 0);
                    picked.set(Calendar.MINUTE, 0);
                    picked.set(Calendar.SECOND, 0);
                    picked.set(Calendar.MILLISECOND, 0);

                    Calendar compareToday = Calendar.getInstance();
                    compareToday.set(Calendar.HOUR_OF_DAY, 0);
                    compareToday.set(Calendar.MINUTE, 0);
                    compareToday.set(Calendar.SECOND, 0);
                    compareToday.set(Calendar.MILLISECOND, 0);

                    Calendar compareMax = Calendar.getInstance();
                    compareMax.set(Calendar.HOUR_OF_DAY, 0);
                    compareMax.set(Calendar.MINUTE, 0);
                    compareMax.set(Calendar.SECOND, 0);
                    compareMax.set(Calendar.MILLISECOND, 0);
                    compareMax.add(Calendar.DAY_OF_YEAR, 7);

                    if (picked.before(compareToday)) {
                        Toast.makeText(this, "You can't reserve past dates", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (picked.after(compareMax)) {
                        Toast.makeText(this, "You can only book within the next 7 days", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedCalendar = picked;
                    updateDateLabel();
                    loadReservationsForSelectedDate();
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMinDate(today.getTimeInMillis());
        dialog.getDatePicker().setMaxDate(maxDay.getTimeInMillis());
        dialog.show();
    }

    private String getSelectedDateString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCalendar.getTime());
    }

    private String getSelectedDateDisplayString() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCalendar.getTime());
    }

    private void updateDateLabel() {
        tvDate.setText("Selected date: " + getSelectedDateDisplayString());
    }

    private void loadCourt(String courtId) {
        progressBar.setVisibility(android.view.View.VISIBLE);

        databaseService.getCourt(courtId, new DatabaseService.DatabaseCallback<Court>() {
            @Override
            public void onCompleted(Court object) {
                court = object;
                if (court == null) {
                    Toast.makeText(CourtDetailsActivity.this, "Court not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                tvCourtName.setText(court.getName());
                tvCourtAddress.setText(court.getAddress());
                tvCourtType.setText(court.getType());

                allSlots.clear();
                allSlots.addAll(TimeSlot.generateDailySlots());

                loadReservationsForSelectedDate();
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(android.view.View.GONE);
                Toast.makeText(CourtDetailsActivity.this, "Failed loading court", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReservationsForSelectedDate() {
        progressBar.setVisibility(android.view.View.VISIBLE);

        databaseService.getReservationsForCourtOnDate(
                court.getId(),
                getSelectedDateString(),
                new DatabaseService.DatabaseCallback<Map<String, Reservation>>() {
                    @Override
                    public void onCompleted(Map<String, Reservation> reservations) {
                        for (TimeSlot slot : allSlots) {
                            slot.setReserved(false);
                            slot.setReservedByUserId("");

                            if (reservations != null && reservations.containsKey(slot.getId())) {
                                Reservation reservation = reservations.get(slot.getId());
                                slot.setReserved(true);
                                if (reservation != null) {
                                    slot.setReservedByUserId(reservation.getUserId());
                                }
                            }
                        }

                        adapter.updateList(allSlots);
                        progressBar.setVisibility(android.view.View.GONE);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        progressBar.setVisibility(android.view.View.GONE);
                        Toast.makeText(CourtDetailsActivity.this, "Failed loading slots", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void showReserveConfirmationDialog(TimeSlot slot) {
        if (slot == null || court == null) return;

        if (slot.isReserved()) {
            Toast.makeText(this, "This slot is unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String message =
                "Court: " + court.getName() + "\n\n" +
                        "Date: " + getSelectedDateDisplayString() + "\n" +
                        "Time: " + slot.getStartTime() + " - " + slot.getEndTime() + "\n\n" +
                        "Do you want to confirm this reservation?";

        new AlertDialog.Builder(this)
                .setTitle("Confirm Reservation")
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> reserveSlot(slot))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void reserveSlot(TimeSlot slot) {
        if (slot.isReserved()) {
            Toast.makeText(this, "This slot is unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedDate = getSelectedDateString();
        String today = DatabaseService.getTodayDate();
        String maxDate = DatabaseService.getDatePlusDays(7);

        if (selectedDate.compareTo(today) < 0) {
            Toast.makeText(this, "You can't reserve past dates", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate.compareTo(maxDate) > 0) {
            Toast.makeText(this, "You can only book within the next 7 days", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String userName = email != null ? email : "User";

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
                        Toast.makeText(CourtDetailsActivity.this, object, Toast.LENGTH_SHORT).show();
                        NotificationHelper.showReservationNotification(
                                CourtDetailsActivity.this,
                                court.getName(),
                                selectedDate,
                                slot.getStartTime(),
                                slot.getEndTime()
                        );
                        loadReservationsForSelectedDate();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(CourtDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        loadReservationsForSelectedDate();
                    }
                }
        );
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}