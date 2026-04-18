package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.adapters.CourtAdapter;
import com.alma.ilaymidler_finalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserPage extends BaseMenuActivity {

    private Spinner spinnerCity;
    private EditText etSearchCourt;
    private RecyclerView rvCourts;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    private final List<Court> allCourts = new ArrayList<>();
    private CourtAdapter courtAdapter;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        setupToolbar(R.id.topToolbar, "Find a Court");

        spinnerCity = findViewById(R.id.spinnerCity);
        etSearchCourt = findViewById(R.id.etSearchCourt);
        rvCourts = findViewById(R.id.rvCourts);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);

        databaseService = DatabaseService.getInstance();

        rvCourts.setLayoutManager(new LinearLayoutManager(this));
        courtAdapter = new CourtAdapter(new ArrayList<>(), court -> {
            Intent intent = new Intent(UserPage.this, CourtDetailsActivity.class);
            intent.putExtra("COURT_ID", court.getId());
            startActivity(intent);
        });
        rvCourts.setAdapter(courtAdapter);

        setupSpinner();
        setupSearch();
        loadCourts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourts();
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.arlocation,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                rvCourts.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Choose a city to view courts");
            }
        });
    }

    private void setupSearch() {
        etSearchCourt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadCourts() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        rvCourts.setVisibility(View.GONE);

        databaseService.getCourtsList(new DatabaseService.DatabaseCallback<List<Court>>() {
            @Override
            public void onCompleted(List<Court> courts) {
                progressBar.setVisibility(View.GONE);
                allCourts.clear();

                if (courts != null) {
                    allCourts.addAll(courts);
                }

                if (allCourts.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("No courts found");
                    rvCourts.setVisibility(View.GONE);
                    return;
                }

                applyFilters();
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                rvCourts.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Failed to load courts");
                Toast.makeText(UserPage.this, "Failed to load courts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        String selectedCity = spinnerCity.getSelectedItem() != null
                ? spinnerCity.getSelectedItem().toString().trim()
                : "";

        String searchText = etSearchCourt.getText() != null
                ? etSearchCourt.getText().toString().trim().toLowerCase(Locale.getDefault())
                : "";

        if (selectedCity.isEmpty() || selectedCity.equals("Choose city")) {
            courtAdapter.updateList(new ArrayList<>());
            rvCourts.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Choose a city to view courts");
            return;
        }

        List<Court> filtered = new ArrayList<>();

        for (Court court : allCourts) {
            String courtCity = court.getCity() != null ? court.getCity().trim() : "";
            String courtName = court.getName() != null ? court.getName().trim() : "";

            boolean cityMatches = courtCity.equalsIgnoreCase(selectedCity);
            boolean nameMatches = searchText.isEmpty()
                    || courtName.toLowerCase(Locale.getDefault()).contains(searchText);

            if (cityMatches && nameMatches) {
                filtered.add(court);
            }
        }

        courtAdapter.updateList(filtered);

        if (filtered.isEmpty()) {
            rvCourts.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);

            if (searchText.isEmpty()) {
                tvEmpty.setText("No courts available in this area");
            } else {
                tvEmpty.setText("No courts match your search");
            }
        } else {
            rvCourts.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }
}