package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.Model.TimeSlot;
import com.alma.ilaymidler_finalproject.services.DatabaseService;

public class AddItem extends AppCompatActivity {

    private EditText etItemName, etItemInfo;
    private Spinner spLocation, sptype;
    private Button btnAddItem;
    private ImageButton btnBack;

    private DatabaseService databaseService;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        /// Initialize views
        InitViews();

        /// Get database instance
        databaseService = DatabaseService.getInstance();


        /// Back button
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AddItem.this, AdminPage.class);
            startActivity(intent);
            finish();
        });

        /// Add item button
        btnAddItem.setOnClickListener(v -> {

            String itemName = etItemName.getText().toString();
            String itemInfo = etItemInfo.getText().toString();
            String itemLocation = spLocation.getSelectedItem().toString();
            String type = sptype.getSelectedItem().toString();

            if (itemName.isEmpty() || itemInfo.isEmpty() ||
                    itemLocation.isEmpty() || type.isEmpty()) {

                Toast.makeText(AddItem.this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            /// Generate ID
            String id = databaseService.generateCourtId();


                /// Create new court
            Court newItem = new Court(id, itemName, itemLocation, type);

            /// Save to DB
            databaseService.createNewCourt(newItem, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Log.d("TAG", "Item added successfully");
                    Toast.makeText(AddItem.this, "המגרש נוסף בהצלחה!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AddItem.this, AdminPage.class));
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e("TAG", "Failed to add item", e);
                    Toast.makeText(AddItem.this, "קרתה שגיאה בהוספה", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    private void InitViews() {
        etItemName = findViewById(R.id.etItemName);
        etItemInfo = findViewById(R.id.etItemInfo);
        spLocation = findViewById(R.id.spLocation);
        sptype = findViewById(R.id.spType);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnBack = findViewById(R.id.btnBack);
    }
}
