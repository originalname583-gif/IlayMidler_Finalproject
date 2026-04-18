package com.alma.ilaymidler_finalproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.adapters.ManageCourtsAdapter;
import com.alma.ilaymidler_finalproject.services.DatabaseService;

import java.util.List;

public class ManageCourtsActivity extends BaseMenuActivity {

    private RecyclerView rvManageCourts;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    private DatabaseService databaseService;
    private ManageCourtsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_courts);

        setupToolbar(R.id.topToolbar, "Manage Courts");

        rvManageCourts = findViewById(R.id.rvManageCourts);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);

        databaseService = DatabaseService.getInstance();

        rvManageCourts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageCourtsAdapter(new ManageCourtsAdapter.OnCourtActionListener() {
            @Override
            public void onEdit(Court court) {
                showEditDialog(court);
            }

            @Override
            public void onDelete(Court court) {
                showDeleteDialog(court);
            }
        });
        rvManageCourts.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourts();
    }

    private void loadCourts() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        rvManageCourts.setVisibility(View.GONE);

        databaseService.getCourtsList(new DatabaseService.DatabaseCallback<List<Court>>() {
            @Override
            public void onCompleted(List<Court> courts) {
                progressBar.setVisibility(View.GONE);

                if (courts == null || courts.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("No courts found");
                    return;
                }

                adapter.updateList(courts);
                rvManageCourts.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Failed to load courts");
                Toast.makeText(ManageCourtsActivity.this, "Failed to load courts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(Court court) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = 40;
        layout.setPadding(pad, pad, pad, pad);

        EditText etName = new EditText(this);
        etName.setHint("Court name");
        etName.setText(court.getName());

        EditText etCity = new EditText(this);
        etCity.setHint("City");
        etCity.setText(court.getCity());

        EditText etAddress = new EditText(this);
        etAddress.setHint("Address");
        etAddress.setText(court.getAddress());

        EditText etType = new EditText(this);
        etType.setHint("Type");
        etType.setText(court.getType());

        layout.addView(etName);
        layout.addView(etCity);
        layout.addView(etAddress);
        layout.addView(etType);

        new AlertDialog.Builder(this)
                .setTitle("Edit Court")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    String newCity = etCity.getText().toString().trim();
                    String newAddress = etAddress.getText().toString().trim();
                    String newType = etType.getText().toString().trim();

                    if (newName.isEmpty() || newCity.isEmpty() || newAddress.isEmpty() || newType.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    court.setName(newName);
                    court.setCity(newCity);
                    court.setAddress(newAddress);
                    court.setType(newType);

                    databaseService.updateCourt(court, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            Toast.makeText(ManageCourtsActivity.this, "Court updated", Toast.LENGTH_SHORT).show();
                            loadCourts();
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(ManageCourtsActivity.this, "Failed to update court", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteDialog(Court court) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Court")
                .setMessage("Are you sure you want to delete " + court.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseService.deleteCourt(court.getId(), new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            Toast.makeText(ManageCourtsActivity.this, "Court deleted", Toast.LENGTH_SHORT).show();
                            loadCourts();
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(ManageCourtsActivity.this, "Failed to delete court", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}