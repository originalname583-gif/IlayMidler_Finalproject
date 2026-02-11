package com.alma.ilaymidler_finalproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddItem extends AppCompatActivity {

    private EditText etItemName, etItemInfo;
    private Spinner spLocation, spType;
    private Button btnAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        etItemName = findViewById(R.id.etItemName);
        etItemInfo = findViewById(R.id.etItemInfo);
        spLocation = findViewById(R.id.spLocation);
        spType = findViewById(R.id.spType);
        btnAddItem = findViewById(R.id.btnAddItem);

        btnAddItem.setOnClickListener(v -> {
            String name = etItemName.getText().toString().trim();
            if(name.isEmpty()) {
                etItemName.setError("אנא מלא שם מגרש");
                return;
            }
            Toast.makeText(this, "מגרש נוסף בהצלחה: " + name, Toast.LENGTH_SHORT).show();
        });
    }
}
