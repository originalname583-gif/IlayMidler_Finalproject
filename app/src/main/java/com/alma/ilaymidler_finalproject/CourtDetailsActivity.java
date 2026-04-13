package com.alma.ilaymidler_finalproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourtDetailsActivity extends AppCompatActivity {

    private TextView tvCourtName, tvCourtAddress, tvCourtType, tvDate;
    private RecyclerView rvTimeSlots;
    private ProgressBar progressBar;

    private DatabaseService databaseService;
    private Court court;
    private TimeSlotAdapter adapter;

    private final List<TimeSlot> allSlots = new ArrayList<>();

    private final androidx.activity.result.ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_details);

        tvCourtName = findViewById(R.id.tvCourtName);
        tvCourtAddress = findViewById(R.id.tvCourtAddress);
        tvCourtType = findViewById(R.id.tvCourtType);
        tvDate = findViewById(R.id.tvDate);
        rvTimeSlots = findViewById(R.id.rvTimeSlots);
        progressBar = findViewById(R.id.progressBar);

        databaseService = DatabaseService.getInstance();
        NotificationHelper.createNotificationChannel(this);
        requestNotificationPermissionIfNeeded();

        rvTimeSlots.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TimeSlotAdapter(new ArrayList<>(), slot -> reserveSlot(slot));
        rvTimeSlots.setAdapter(adapter);

        String courtId = getIntent().getStringExtra("COURT_ID");
        if (courtId == null || courtId.trim().isEmpty()) {
            Toast.makeText(this, "Court not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDate.setText("Date: " + DatabaseService.getTodayDate());
        loadCourt(courtId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (court != null) {
            loadReservationsForToday();
        }
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

                loadReservationsForToday();
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(android.view.View.GONE);
                Toast.makeText(CourtDetailsActivity.this, "Failed loading court", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReservationsForToday() {
        progressBar.setVisibility(android.view.View.VISIBLE);

        databaseService.getReservationsForCourtOnDate(
                court.getId(),
                DatabaseService.getTodayDate(),
                new DatabaseService.DatabaseCallback<Map<String, Reservation>>() {
                    @Override
                    public void onCompleted(Map<String, Reservation> reservations) {
                        for (TimeSlot slot : allSlots) {
                            slot.setReserved(false);
                            slot.setReservedByUserId(null);

                            if (reservations.containsKey(slot.getId())) {
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

    private void reserveSlot(TimeSlot slot) {
        if (slot.isReserved()) {
            Toast.makeText(this, "This slot is unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String userName = email != null ? email : "User";

        databaseService.reserveCourtSlot(
                court,
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
                                slot.getStartTime(),
                                slot.getEndTime()
                        );
                        loadReservationsForToday();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(CourtDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        loadReservationsForToday();
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