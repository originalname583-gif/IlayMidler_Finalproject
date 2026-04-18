package com.alma.ilaymidler_finalproject;

import android.os.Bundle;
import android.widget.Button;

public class About extends BaseMenuActivity {

    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupToolbar(R.id.topToolbar, "About");

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}