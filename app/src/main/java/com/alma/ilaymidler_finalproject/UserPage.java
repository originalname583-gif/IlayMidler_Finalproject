package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.adapters.CourtAdapter;
import com.alma.ilaymidler_finalproject.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class UserPage extends BaseMenuActivity {

    private Spinner spinnerCity;
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

        setupToolbar(R.id.topToolbar, "User Page");

        spinnerCity = findViewById(R.id.spinnerCity);
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
                String selectedCity = parent.getItemAtPosition(position).toString();
                filterCourtsByCity(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                rvCourts.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Choose a city to view courts");
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
                    tvEmpty.setText("No courts were added yet.");
                    return;
                }

                filterCourtsByCity(spinnerCity.getSelectedItem().toString());
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Something went wrong while loading courts.");
            }
        });
    }

    private void filterCourtsByCity(String city) {
        List<Court> filtered = new ArrayList<>();

        for (Court court : allCourts) {
            if (court.getCity() != null &&
                    court.getCity().trim().equalsIgnoreCase(city.trim())) {
                filtered.add(court);
            }
        }

        courtAdapter.updateList(filtered);

        if (filtered.isEmpty()) {
            rvCourts.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("No courts are available in this city.");
        } else {
            rvCourts.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }
}