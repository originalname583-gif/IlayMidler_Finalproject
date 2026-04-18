package com.alma.ilaymidler_finalproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.services.DatabaseService;

public class AddItem extends BaseMenuActivity {

    private EditText etItemName, etItemInfo;
    private Spinner spLocation, spType;
    private Button btnAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        setupToolbar(R.id.topToolbar, "Add Court");

        etItemName = findViewById(R.id.etItemName);
        etItemInfo = findViewById(R.id.etItemInfo);
        spLocation = findViewById(R.id.spLocation);
        spType = findViewById(R.id.spType);
        btnAddItem = findViewById(R.id.btnAddItem);

        btnAddItem.setOnClickListener(v -> {
            String name = etItemName.getText().toString().trim();
            String info = etItemInfo.getText().toString().trim();
            String location = spLocation.getSelectedItem() != null ? spLocation.getSelectedItem().toString() : "";
            String type = spType.getSelectedItem() != null ? spType.getSelectedItem().toString() : "";

            if (name.isEmpty()) {
                etItemName.setError("Please enter a court name");
                return;
            }

            if (info.isEmpty()) {
                etItemInfo.setError("Please enter court info");
                return;
            }

            if (location.equals("Choose city")) {
                Toast.makeText(this, "Please choose a city", Toast.LENGTH_SHORT).show();
                return;
            }

            if (type.equals("Choose type")) {
                Toast.makeText(this, "Please choose a court type", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseService db = DatabaseService.getInstance();
            String courtId = db.generateCourtId();
            Court court = new Court(courtId, name, location, info, type);

            db.createNewCourt(court, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(AddItem.this, "Court added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(AddItem.this, "Failed to add court", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}