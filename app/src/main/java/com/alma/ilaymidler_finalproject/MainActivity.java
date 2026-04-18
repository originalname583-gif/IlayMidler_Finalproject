package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends BaseMenuActivity implements View.OnClickListener {

    Button btnAbout, btnRegister, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar(R.id.topToolbar, "FieldTime");

        btnAbout = findViewById(R.id.btnAbout);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        btnAbout.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAbout)
            startActivity(new Intent(this, About.class));
        else if (v.getId() == R.id.btnRegister)
            startActivity(new Intent(this, Register.class));
        else if (v.getId() == R.id.btnLogin)
            startActivity(new Intent(this, Login.class));
    }
}