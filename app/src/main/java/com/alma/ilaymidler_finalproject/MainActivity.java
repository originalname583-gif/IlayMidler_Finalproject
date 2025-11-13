package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnAbout, btnRegister, btnLogin;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnAbout = findViewById(R.id.btnAbout);
        btnRegister = findViewById(R.id.btnRegister); // make sure you have this button in your XML
        btnLogin = findViewById(R.id.btnLogin);       // make sure you have this button in your XML

        btnAbout.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAbout) {
            intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btnRegister) {
            intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btnLogin) {
            intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
    }
}
