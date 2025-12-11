package com.alma.ilaymidler_finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPage extends AppCompatActivity {

    Spinner spinnerCity;
    LinearLayout fieldsContainer;
    Map<String, List<String>> cityFieldsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        spinnerCity = findViewById(R.id.spinnerCity);
        fieldsContainer = findViewById(R.id.fieldsContainer);

        cityFieldsMap = new HashMap<>();

        // Example: תל–אביב / גוש דן
        cityFieldsMap.put("תל–אביב / גוש דן", new ArrayList<String>() {{
            add("תל אביב – ספורטק צפון");
            add("תל אביב – ספורטק מרכז / דרום");
            add("תל אביב – מגרש שמשון");
            add("תל אביב – גדנ״ע ת״א");
            add("תל אביב – כפר שלם");
            add("יפו – גאון");
            add("יפו – צ’רנר");
        }});

        // You can add the rest of your cities in the same way
        cityFieldsMap.put("דרום", new ArrayList<String>() {{
            add("באר שבע – אצטדיון טוטו");
            add("באר שבע – אימונים (רייסר)");
        }});

        // Spinner adapter with placeholder
        List<String> cities = new ArrayList<>(cityFieldsMap.keySet());
        cities.add(0, "בחר עיר"); // placeholder
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cities);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(spinnerAdapter);

        // Spinner listener
        spinnerCity.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    fieldsContainer.removeAllViews(); // Clear container if placeholder
                } else {
                    String selectedCity = (String) parent.getItemAtPosition(position);
                    showFieldsForCity(selectedCity);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                fieldsContainer.removeAllViews();
            }
        });
    }

    private void showFieldsForCity(String city) {
        fieldsContainer.removeAllViews();
        List<String> fields = cityFieldsMap.get(city);
        if (fields == null) return;

        for (String field : fields) {
            // Horizontal layout for each field and button
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 10, 0, 10);

            // Field name
            TextView tv = new TextView(this);
            tv.setText(field);
            tv.setTextSize(16f);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            // Button
            Button btn = new Button(this);
            btn.setText("זמנים פנויים");
            btn.setOnClickListener(v -> Toast.makeText(UserPage.this, "זמנים פנויים ל: " + field, Toast.LENGTH_SHORT).show());

            row.addView(tv);
            row.addView(btn);

            fieldsContainer.addView(row);
        }
    }
}
