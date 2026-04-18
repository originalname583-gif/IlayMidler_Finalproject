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

import java.util.ArrayList;
import java.util.List;

public class MyReservationsActivity extends BaseMenuActivity {

    private RecyclerView rvMyReservations;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    private DatabaseService databaseService;
    private MyReservationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        setupToolbar(R.id.topToolbar, "My Reservations");

        rvMyReservations = findViewById(R.id.rvMyReservations);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);

        databaseService = DatabaseService.getInstance();

        rvMyReservations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyReservationsAdapter(this::showCancelDialog);
        rvMyReservations.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyReservations();
    }

    private void loadMyReservations() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressBar.setVisibility(View.VISIBLE);
        rvMyReservations.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);

        databaseService.getReservationsForUser(userId, new DatabaseService.DatabaseCallback<List<Reservation>>() {
            @Override
            public void onCompleted(List<Reservation> reservations) {
                if (reservations == null || reservations.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    rvMyReservations.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("You have no reservations yet");
                    return;
                }

                loadCourtDetailsForReservations(reservations);
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Failed to load reservations");
                Toast.makeText(MyReservationsActivity.this, "Failed to load reservations", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCourtDetailsForReservations(List<Reservation> reservations) {
        List<ReservationDisplayItem> displayItems = new ArrayList<>();

        if (reservations.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("You have no reservations yet");
            return;
        }

        final int total = reservations.size();
        final int[] loadedCount = {0};

        for (Reservation reservation : reservations) {
            databaseService.getCourt(reservation.getCourtId(), new DatabaseService.DatabaseCallback<Court>() {
                @Override
                public void onCompleted(Court court) {
                    displayItems.add(new ReservationDisplayItem(reservation, court));
                    loadedCount[0]++;
                    checkIfDone(displayItems, total);
                }

                @Override
                public void onFailed(Exception e) {
                    displayItems.add(new ReservationDisplayItem(reservation, null));
                    loadedCount[0]++;
                    checkIfDone(displayItems, total);
                }

                private void checkIfDone(List<ReservationDisplayItem> items, int totalItems) {
                    if (loadedCount[0] == totalItems) {
                        progressBar.setVisibility(View.GONE);

                        if (items.isEmpty()) {
                            rvMyReservations.setVisibility(View.GONE);
                            tvEmpty.setVisibility(View.VISIBLE);
                            tvEmpty.setText("You have no reservations yet");
                        } else {
                            adapter.updateList(items);
                            rvMyReservations.setVisibility(View.VISIBLE);
                            tvEmpty.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    private void showCancelDialog(ReservationDisplayItem item) {
        if (item == null || item.getReservation() == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes", (dialog, which) -> cancelReservation(item.getReservation()))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelReservation(Reservation reservation) {
        progressBar.setVisibility(View.VISIBLE);

        databaseService.cancelReservation(reservation, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(MyReservationsActivity.this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                loadMyReservations();
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MyReservationsActivity.this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
            }
        });
    }
}